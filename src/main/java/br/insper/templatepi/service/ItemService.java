package br.insper.templatepi.service;

import br.insper.templatepi.client.UsuarioClient;
import br.insper.templatepi.entity.Item;
import br.insper.templatepi.exception.ItemNaoEncontradoException;
import br.insper.templatepi.observer.ItemObserver;
import br.insper.templatepi.repository.ItemRepository;
import br.insper.templatepi.validator.ValidadorItem;
import br.insper.templatepi.validator.ValidadorItemFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

// TODO(PI): concentre nesta classe as regras de negócio específicas da prova.
@Service
public class ItemService {

	private static final String EVENTO_CRIADO = "CRIADO";
	private static final String EVENTO_EXCLUIDO = "EXCLUIDO";

	private final ItemRepository itemRepository;
	private final UsuarioClient usuarioClient;
	private final ValidadorItemFactory validadorFactory;
	private final List<ItemObserver> observers;

	public ItemService(
			ItemRepository itemRepository,
			UsuarioClient usuarioClient,
			ValidadorItemFactory validadorFactory,
			List<ItemObserver> observers) {
		this.itemRepository = itemRepository;
		this.usuarioClient = usuarioClient;
		this.validadorFactory = validadorFactory;
		this.observers = observers;
	}

	public Item criar(Item item) {
		ValidadorItem validador = validadorFactory.obter(item.getTipo());
		validador.validar(item);

		item.setId(null);
		item.setNome(item.getNome().trim());
		item.setClienteId(item.getClienteId().trim());
		item.setUrlAcesso(normalizarTextoOpcional(item.getUrlAcesso()));
		item.setEmailCliente(usuarioClient.buscarEmail(item.getClienteId()));
		item.setValorTotal(calcularValorTotal(item));

		Item salvo = itemRepository.save(item);
		notificarObservadores(salvo, EVENTO_CRIADO);
		return salvo;
	}

	public List<Item> listar(String clienteId) {
		if (clienteId == null || clienteId.isBlank()) {
			return itemRepository.findAllByOrderByDataCriacaoDesc();
		}
		return itemRepository.findByClienteIdOrderByDataCriacaoDesc(clienteId.trim());
	}

	public void deletar(Long id) {
		Item item = itemRepository.findById(id)
				.orElseThrow(() -> new ItemNaoEncontradoException(id));
		itemRepository.delete(item);
		notificarObservadores(item, EVENTO_EXCLUIDO);
	}

	private BigDecimal calcularValorTotal(Item item) {
		long quantidade = item.getQuantidade() == null ? 1L : item.getQuantidade();
		return item.getPrecoUnitario().multiply(BigDecimal.valueOf(quantidade));
	}

	private void notificarObservadores(Item item, String evento) {
		for (ItemObserver observer : observers) {
			observer.atualizar(item, evento);
		}
	}

	private String normalizarTextoOpcional(String texto) {
		return texto == null ? null : texto.trim();
	}
}
