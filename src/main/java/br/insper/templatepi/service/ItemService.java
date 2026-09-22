package br.insper.templatepi.service;

import br.insper.templatepi.dto.ItemRequest;
import br.insper.templatepi.dto.ItemResponse;
import br.insper.templatepi.entity.Item;
import br.insper.templatepi.exception.ItemNaoEncontradoException;
import br.insper.templatepi.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

// TODO(PI): concentre nesta classe as regras de negócio específicas da prova.
@Service
public class ItemService {

	private final ItemRepository itemRepository;

	public ItemService(ItemRepository itemRepository) {
		this.itemRepository = itemRepository;
	}

	public ItemResponse criar(ItemRequest request) {
		Item item = new Item(
				request.getNome().trim(),
				request.getDescricao().trim(),
				request.getQuantidade()
		);
		return ItemResponse.fromEntity(itemRepository.save(item));
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
}
