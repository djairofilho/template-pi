package br.insper.templatepi.service;

import br.insper.templatepi.dto.ItemRequest;
import br.insper.templatepi.dto.ItemResponse;
import br.insper.templatepi.entity.Item;
import br.insper.templatepi.exception.ItemNaoEncontradoException;
import br.insper.templatepi.repository.ItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// TODO(PI): mantenha estes padrões e troque os cenários pelas regras da prova.
@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

	@Mock
	private ItemRepository itemRepository;

	@InjectMocks
	private ItemService itemService;

	@Test
	void deveCriarItemComTextoNormalizado() {
		LocalDateTime dataCriacao = LocalDateTime.of(2026, 9, 22, 12, 0);
		when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> {
			Item item = invocation.getArgument(0);
			item.setId(1L);
			item.setDataCriacao(dataCriacao);
			return item;
		});

		ItemResponse response = itemService.criar(new ItemRequest(
				"  Item de exemplo  ",
				"  Descrição do item  ",
				10
		));

		ArgumentCaptor<Item> captor = ArgumentCaptor.forClass(Item.class);
		verify(itemRepository).save(captor.capture());
		Item salvo = captor.getValue();
		assertThat(salvo.getNome()).isEqualTo("Item de exemplo");
		assertThat(salvo.getDescricao()).isEqualTo("Descrição do item");
		assertThat(salvo.getQuantidade()).isEqualTo(10);
		assertThat(salvo.isDeletado()).isFalse();
		assertThat(response.getId()).isEqualTo(1L);
		assertThat(response.getNome()).isEqualTo("Item de exemplo");
		assertThat(response.getDescricao()).isEqualTo("Descrição do item");
		assertThat(response.getQuantidade()).isEqualTo(10);
		assertThat(response.getDataCriacao()).isEqualTo(dataCriacao);
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = "   ")
	void deveListarTodosQuandoFiltroNaoTemTexto(String nome) {
		Item item = item(1L, "Algoritmo");
		when(itemRepository.findByDeletadoFalseOrderByNomeAsc())
				.thenReturn(List.of(item));

		List<ItemResponse> response = itemService.listar(nome);

		assertThat(response).singleElement()
				.extracting(ItemResponse::getNome)
				.isEqualTo("Algoritmo");
		verify(itemRepository).findByDeletadoFalseOrderByNomeAsc();
	}

	@Test
	void deveListarItensPeloInicioDoNome() {
		Item item = item(2L, "Item de exemplo");
		when(itemRepository
				.findByDeletadoFalseAndNomeStartingWithIgnoreCaseOrderByNomeAsc("Item"))
				.thenReturn(List.of(item));

		List<ItemResponse> response = itemService.listar("  Item  ");

		assertThat(response).singleElement()
				.extracting(ItemResponse::getId)
				.isEqualTo(2L);
		verify(itemRepository)
				.findByDeletadoFalseAndNomeStartingWithIgnoreCaseOrderByNomeAsc("Item");
	}

	@Test
	void deveRetornarListaVaziaQuandoNaoExistemItens() {
		when(itemRepository.findByDeletadoFalseOrderByNomeAsc()).thenReturn(List.of());

		assertThat(itemService.listar(null)).isEmpty();

		verify(itemRepository).findByDeletadoFalseOrderByNomeAsc();
	}

	@Test
	void deveDeletarItemLogicamente() {
		Item item = item(3L, "Item removível");
		when(itemRepository.findByIdAndDeletadoFalse(3L)).thenReturn(Optional.of(item));

		itemService.deletar(3L);

		assertThat(item.isDeletado()).isTrue();
		verify(itemRepository).findByIdAndDeletadoFalse(3L);
		verify(itemRepository).save(item);
	}

	@Test
	void deveFalharAoDeletarItemInexistenteOuJaDeletado() {
		when(itemRepository.findByIdAndDeletadoFalse(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> itemService.deletar(99L))
				.isInstanceOf(ItemNaoEncontradoException.class)
				.hasMessage("Item com ID 99 não encontrado");

		verify(itemRepository).findByIdAndDeletadoFalse(99L);
	}

	private Item item(Long id, String nome) {
		Item item = new Item(nome, "Descrição", 10);
		item.setId(id);
		item.setDataCriacao(LocalDateTime.of(2026, 9, 22, 12, 0));
		return item;
	}
}
