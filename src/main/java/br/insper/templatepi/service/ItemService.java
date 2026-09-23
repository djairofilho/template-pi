package br.insper.templatepi.service;

import br.insper.templatepi.entity.Item;
import br.insper.templatepi.exception.ItemNaoEncontradoException;
import br.insper.templatepi.observer.ItemObserver;
import br.insper.templatepi.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {

	private static final String EVENTO_CRIADO = "CRIADO";
	private static final String EVENTO_EXCLUIDO = "EXCLUIDO";

	private final ItemRepository itemRepository;
	private final List<ItemObserver> observers;

	public ItemService(
			ItemRepository itemRepository,
			List<ItemObserver> observers) {
		this.itemRepository = itemRepository;
		this.observers = observers;
	}

	public Item criar(Item item) {
		item.setId(null);
		item.setAutor(item.getAutor().trim());
		item.setConteudo(item.getConteudo().trim());

		Item salvo = itemRepository.save(item);
		notificarObservadores(salvo, EVENTO_CRIADO);
		return salvo;
	}

	public List<Item> listar() {
		return itemRepository.findAllByOrderByDataAvaliacaoDesc();
	}

	public void deletar(Long id) {
		Item item = itemRepository.findById(id)
				.orElseThrow(() -> new ItemNaoEncontradoException(id));
		itemRepository.delete(item);
		notificarObservadores(item, EVENTO_EXCLUIDO);
	}

	private void notificarObservadores(Item item, String evento) {
		for (ItemObserver observer : observers) {
			observer.atualizar(item, evento);
		}
	}
}
