package br.insper.templatepi.service;

import br.insper.templatepi.dto.ItemRequest;
import br.insper.templatepi.dto.ItemResponse;
import br.insper.templatepi.dto.ProcessamentoItemResponse;
import br.insper.templatepi.entity.Item;
import br.insper.templatepi.entity.StatusItem;
import br.insper.templatepi.exception.ItemNaoEncontradoException;
import br.insper.templatepi.observer.ItemObserver;
import br.insper.templatepi.processor.ProcessadorItem;
import br.insper.templatepi.processor.ProcessadorItemFactory;
import br.insper.templatepi.repository.ItemRepository;
import br.insper.templatepi.validator.ValidadorItem;
import br.insper.templatepi.validator.ValidadorItemFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

// TODO(PI): concentre nesta classe as regras de negócio específicas da prova.
@Service
public class ItemService {

	private final ItemRepository itemRepository;
	private final ValidadorItemFactory validadorFactory;
	private final ProcessadorItemFactory processadorFactory;
	private final List<ItemObserver> observers;

	public ItemService(
			ItemRepository itemRepository,
			ValidadorItemFactory validadorFactory,
			ProcessadorItemFactory processadorFactory,
			List<ItemObserver> observers) {
		this.itemRepository = itemRepository;
		this.validadorFactory = validadorFactory;
		this.processadorFactory = processadorFactory;
		this.observers = observers;
	}

	public ItemResponse criar(ItemRequest request) {
		ValidadorItem validador = validadorFactory.obter(request.getTipo());
		validador.validar(request);
		Item item = new Item(
				request.getNome().trim(),
				request.getDescricao().trim(),
				request.getTipo(),
				request.getQuantidade(),
				normalizarTextoOpcional(request.getUrlAcesso()),
				request.getDuracaoMinutos()
		);
		Item salvo = itemRepository.save(item);
		notificarObservadores(salvo, null);
		return ItemResponse.fromEntity(salvo);
	}

	public List<ItemResponse> listar(String nome) {
		List<Item> itens;
		if (nome == null || nome.isBlank()) {
			itens = itemRepository.findByDeletadoFalseOrderByNomeAsc();
		} else {
			itens = itemRepository
					.findByDeletadoFalseAndNomeStartingWithIgnoreCaseOrderByNomeAsc(nome.trim());
		}

		return itens.stream()
				.map(ItemResponse::fromEntity)
				.toList();
	}

	public void deletar(Long id) {
		Item item = itemRepository.findByIdAndDeletadoFalse(id)
				.orElseThrow(() -> new ItemNaoEncontradoException(id));
		item.setDeletado(true);
		itemRepository.save(item);
	}

	public ProcessamentoItemResponse processar(Long id) {
		Item item = itemRepository.findByIdAndDeletadoFalse(id)
				.orElseThrow(() -> new ItemNaoEncontradoException(id));
		StatusItem statusAnterior = item.getStatus();
		String mensagem;
		boolean sucesso;

		try {
			ProcessadorItem processador = processadorFactory.obter(item.getTipo());
			mensagem = processador.processar(item);
			item.setStatus(StatusItem.PROCESSADO);
			sucesso = true;
		} catch (RuntimeException exception) {
			mensagem = exception.getMessage();
			item.setStatus(StatusItem.FALHA);
			sucesso = false;
		}

		item.setDataProcessamento(LocalDateTime.now());
		Item salvo = itemRepository.save(item);
		notificarObservadores(salvo, statusAnterior);
		return new ProcessamentoItemResponse(sucesso, mensagem, ItemResponse.fromEntity(salvo));
	}

	private void notificarObservadores(Item item, StatusItem statusAnterior) {
		for (ItemObserver observer : observers) {
			observer.atualizar(item, statusAnterior, item.getStatus());
		}
	}

	private String normalizarTextoOpcional(String texto) {
		return texto == null ? null : texto.trim();
	}
}
