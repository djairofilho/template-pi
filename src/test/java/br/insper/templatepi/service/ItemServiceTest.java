package br.insper.templatepi.service;

import br.insper.templatepi.entity.Item;
import br.insper.templatepi.exception.ItemNaoEncontradoException;
import br.insper.templatepi.observer.ItemObserver;
import br.insper.templatepi.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

	@Mock
	private ItemRepository itemRepository;

	@Mock
	private ItemObserver observer;

	private ItemService itemService;

	@BeforeEach
	void configurarService() {
		itemService = new ItemService(itemRepository, List.of(observer));
	}

	@Test
	void deveCriarAvaliacaoNormalizandoTextos() {
		Item item = new Item("  Maria  ", "  Excelente atendimento  ", 5);
		when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> {
			Item salvo = invocation.getArgument(0);
			salvo.setId(1L);
			return salvo;
		});

		Item resposta = itemService.criar(item);

		ArgumentCaptor<Item> captor = ArgumentCaptor.forClass(Item.class);
		verify(itemRepository).save(captor.capture());
		Item salvo = captor.getValue();
		assertThat(salvo.getAutor()).isEqualTo("Maria");
		assertThat(salvo.getConteudo()).isEqualTo("Excelente atendimento");
		assertThat(salvo.getNota()).isEqualTo(5);
		assertThat(resposta.getId()).isEqualTo(1L);
		verify(observer).atualizar(salvo, "CRIADO");
	}

	@Test
	void deveIgnorarIdEnviadoAoCriarAvaliacao() {
		Item item = new Item("João", "Bom", 4);
		item.setId(999L);
		when(itemRepository.save(item)).thenReturn(item);

		itemService.criar(item);

		assertThat(item.getId()).isNull();
	}

	@Test
	void deveListarAvaliacoesDaMaisRecenteParaAMaisAntiga() {
		Item item = new Item("Ana", "Muito bom", 5);
		when(itemRepository.findAllByOrderByDataAvaliacaoDesc()).thenReturn(List.of(item));

		assertThat(itemService.listar()).containsExactly(item);

		verify(itemRepository).findAllByOrderByDataAvaliacaoDesc();
	}

	@Test
	void deveRetornarListaVaziaQuandoNaoExistemAvaliacoes() {
		when(itemRepository.findAllByOrderByDataAvaliacaoDesc()).thenReturn(List.of());

		assertThat(itemService.listar()).isEmpty();
	}

	@Test
	void deveDeletarAvaliacaoEInformarObservers() {
		Item item = new Item("Ana", "Bom", 4);
		item.setId(3L);
		when(itemRepository.findById(3L)).thenReturn(Optional.of(item));

		itemService.deletar(3L);

		verify(itemRepository).delete(item);
		verify(observer).atualizar(item, "EXCLUIDO");
	}

	@Test
	void deveFalharAoDeletarAvaliacaoInexistente() {
		when(itemRepository.findById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> itemService.deletar(99L))
				.isInstanceOf(ItemNaoEncontradoException.class)
				.hasMessage("Item com ID 99 não encontrado");
	}
}
