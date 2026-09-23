package br.insper.templatepi.service;

import br.insper.templatepi.client.UsuarioClient;
import br.insper.templatepi.entity.Item;
import br.insper.templatepi.entity.TipoItem;
import br.insper.templatepi.exception.ItemNaoEncontradoException;
import br.insper.templatepi.observer.ItemObserver;
import br.insper.templatepi.repository.ItemRepository;
import br.insper.templatepi.validator.ValidadorItem;
import br.insper.templatepi.validator.ValidadorItemFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// TODO(PI): troque os cenários pelas regras específicas do enunciado.
@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

	@Mock
	private ItemRepository itemRepository;

	@Mock
	private UsuarioClient usuarioClient;

	@Mock
	private ValidadorItemFactory validadorFactory;

	@Mock
	private ValidadorItem validador;

	@Mock
	private ItemObserver observer;

	private ItemService itemService;

	@BeforeEach
	void configurarService() {
		itemService = new ItemService(
				itemRepository,
				usuarioClient,
				validadorFactory,
				List.of(observer)
		);
	}

	@Test
	void deveCriarItemComClienteValidadoEValorTotalCalculado() {
		Item item = item("  Item físico  ", TipoItem.FISICO, "  cliente-1  ", 10, null, null, "2.50");
		when(validadorFactory.obter(TipoItem.FISICO)).thenReturn(validador);
		when(usuarioClient.buscarEmail("cliente-1")).thenReturn("cliente@exemplo.com");
		when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> {
			Item salvo = invocation.getArgument(0);
			salvo.setId(1L);
			salvo.setDataCriacao(LocalDateTime.of(2026, 9, 23, 10, 0));
			return salvo;
		});

		Item response = itemService.criar(item);

		ArgumentCaptor<Item> captor = ArgumentCaptor.forClass(Item.class);
		verify(validadorFactory).obter(TipoItem.FISICO);
		verify(validador).validar(item);
		verify(itemRepository).save(captor.capture());
		Item salvo = captor.getValue();
		assertThat(salvo.getNome()).isEqualTo("Item físico");
		assertThat(salvo.getClienteId()).isEqualTo("cliente-1");
		assertThat(salvo.getEmailCliente()).isEqualTo("cliente@exemplo.com");
		assertThat(salvo.getValorTotal()).isEqualByComparingTo("25.00");
		assertThat(response.getId()).isEqualTo(1L);
		verify(observer).atualizar(salvo, "CRIADO");
	}

	@Test
	void deveUsarUmaUnidadeEValidarTextoOpcionalParaItemSemQuantidade() {
		Item item = item(
				"Arquivo",
				TipoItem.DIGITAL,
				"cliente-2",
				null,
				"  https://exemplo.com  ",
				null,
				"12.00"
		);
		when(validadorFactory.obter(TipoItem.DIGITAL)).thenReturn(validador);
		when(usuarioClient.buscarEmail("cliente-2")).thenReturn("digital@exemplo.com");
		when(itemRepository.save(item)).thenReturn(item);

		Item response = itemService.criar(item);

		assertThat(response.getUrlAcesso()).isEqualTo("https://exemplo.com");
		assertThat(response.getValorTotal()).isEqualByComparingTo("12.00");
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = "   ")
	void deveListarTodosQuandoFiltroNaoTemTexto(String clienteId) {
		Item item = item("Item", TipoItem.FISICO, "cliente-1", 1, null, null, "1.00");
		when(itemRepository.findAllByOrderByDataCriacaoDesc()).thenReturn(List.of(item));

		assertThat(itemService.listar(clienteId)).containsExactly(item);

		verify(itemRepository).findAllByOrderByDataCriacaoDesc();
	}

	@Test
	void deveListarItensDoClienteInformado() {
		Item item = item("Item", TipoItem.FISICO, "cliente-1", 1, null, null, "1.00");
		when(itemRepository.findByClienteIdOrderByDataCriacaoDesc("cliente-1"))
				.thenReturn(List.of(item));

		assertThat(itemService.listar("  cliente-1  ")).containsExactly(item);

		verify(itemRepository).findByClienteIdOrderByDataCriacaoDesc("cliente-1");
	}

	@Test
	void deveRetornarListaVaziaQuandoNaoExistemItens() {
		when(itemRepository.findAllByOrderByDataCriacaoDesc()).thenReturn(List.of());

		assertThat(itemService.listar(null)).isEmpty();
	}

	@Test
	void deveDeletarItemEInformarObservers() {
		Item item = item("Item", TipoItem.FISICO, "cliente-1", 1, null, null, "1.00");
		item.setId(3L);
		when(itemRepository.findById(3L)).thenReturn(Optional.of(item));

		itemService.deletar(3L);

		verify(itemRepository).delete(item);
		verify(observer).atualizar(item, "EXCLUIDO");
	}

	@Test
	void deveFalharAoDeletarItemInexistente() {
		when(itemRepository.findById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> itemService.deletar(99L))
				.isInstanceOf(ItemNaoEncontradoException.class)
				.hasMessage("Item com ID 99 não encontrado");
	}

	private Item item(
			String nome,
			TipoItem tipo,
			String clienteId,
			Integer quantidade,
			String url,
			Integer duracao,
			String preco) {
		return new Item(
				nome,
				tipo,
				clienteId,
				quantidade,
				url,
				duracao,
				new BigDecimal(preco)
		);
	}
}
