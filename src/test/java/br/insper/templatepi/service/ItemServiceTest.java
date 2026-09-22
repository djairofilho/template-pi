package br.insper.templatepi.service;

import br.insper.templatepi.dto.ItemRequest;
import br.insper.templatepi.dto.ItemResponse;
import br.insper.templatepi.dto.ProcessamentoItemResponse;
import br.insper.templatepi.entity.Item;
import br.insper.templatepi.entity.StatusItem;
import br.insper.templatepi.entity.TipoItem;
import br.insper.templatepi.exception.ItemNaoEncontradoException;
import br.insper.templatepi.observer.ItemObserver;
import br.insper.templatepi.processor.ProcessadorItem;
import br.insper.templatepi.processor.ProcessadorItemFactory;
import br.insper.templatepi.repository.ItemRepository;
import br.insper.templatepi.validator.ValidadorItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
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

	@Mock
	private ValidadorItem validadorItem;

	@Mock
	private ProcessadorItemFactory processadorFactory;

	@Mock
	private ProcessadorItem processador;

	@Mock
	private ItemObserver observer;

	private ItemService itemService;

	@BeforeEach
	void configurarService() {
		itemService = new ItemService(
				itemRepository,
				validadorItem,
				processadorFactory,
				List.of(observer)
		);
	}

	@Test
	void deveCriarItemDigitalComTextoNormalizado() {
		LocalDateTime dataCriacao = LocalDateTime.of(2026, 9, 22, 12, 0);
		ItemRequest request = new ItemRequest(
				"  Item de exemplo  ",
				"  Descrição do item  ",
				TipoItem.DIGITAL,
				null,
				"  https://exemplo.com/item  ",
				null
		);
		when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> {
			Item item = invocation.getArgument(0);
			item.setId(1L);
			item.setDataCriacao(dataCriacao);
			return item;
		});

		ItemResponse response = itemService.criar(request);

		ArgumentCaptor<Item> captor = ArgumentCaptor.forClass(Item.class);
		verify(validadorItem).validar(request);
		verify(itemRepository).save(captor.capture());
		Item salvo = captor.getValue();
		assertThat(salvo.getNome()).isEqualTo("Item de exemplo");
		assertThat(salvo.getDescricao()).isEqualTo("Descrição do item");
		assertThat(salvo.getTipo()).isEqualTo(TipoItem.DIGITAL);
		assertThat(salvo.getUrlAcesso()).isEqualTo("https://exemplo.com/item");
		assertThat(salvo.getStatus()).isEqualTo(StatusItem.PENDENTE);
		assertThat(salvo.isDeletado()).isFalse();
		assertThat(response.getId()).isEqualTo(1L);
		assertThat(response.getStatus()).isEqualTo(StatusItem.PENDENTE);
		assertThat(response.getDataCriacao()).isEqualTo(dataCriacao);
		verify(observer).atualizar(salvo, null, StatusItem.PENDENTE);
	}

	@Test
	void devePreservarUrlNulaAoCriarItemFisico() {
		ItemRequest request = new ItemRequest(
				"Item físico",
				"Descrição",
				TipoItem.FISICO,
				10,
				null,
				null
		);
		when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

		ItemResponse response = itemService.criar(request);

		assertThat(response.getUrlAcesso()).isNull();
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

	@Test
	void deveProcessarItemComStrategySelecionadaPelaFactory() {
		Item item = item(4L, "Item processável");
		when(itemRepository.findByIdAndDeletadoFalse(4L)).thenReturn(Optional.of(item));
		when(processadorFactory.obter(TipoItem.FISICO)).thenReturn(processador);
		when(processador.processar(item)).thenReturn("Item processado");
		when(itemRepository.save(item)).thenReturn(item);

		ProcessamentoItemResponse response = itemService.processar(4L);

		assertThat(response.isSucesso()).isTrue();
		assertThat(response.getMensagem()).isEqualTo("Item processado");
		assertThat(response.getItem().getStatus()).isEqualTo(StatusItem.PROCESSADO);
		assertThat(response.getItem().getDataProcessamento()).isNotNull();
		verify(observer).atualizar(item, StatusItem.PENDENTE, StatusItem.PROCESSADO);
	}

	@Test
	void deveRegistrarFalhaQuandoStrategyLancaExcecao() {
		Item item = item(5L, "Item com falha");
		when(itemRepository.findByIdAndDeletadoFalse(5L)).thenReturn(Optional.of(item));
		when(processadorFactory.obter(TipoItem.FISICO)).thenReturn(processador);
		when(processador.processar(item)).thenThrow(new IllegalStateException("Falha simulada"));
		when(itemRepository.save(item)).thenReturn(item);

		ProcessamentoItemResponse response = itemService.processar(5L);

		assertThat(response.isSucesso()).isFalse();
		assertThat(response.getMensagem()).isEqualTo("Falha simulada");
		assertThat(response.getItem().getStatus()).isEqualTo(StatusItem.FALHA);
		verify(observer).atualizar(item, StatusItem.PENDENTE, StatusItem.FALHA);
	}

	@Test
	void deveFalharAoProcessarItemInexistente() {
		when(itemRepository.findByIdAndDeletadoFalse(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> itemService.processar(99L))
				.isInstanceOf(ItemNaoEncontradoException.class);
	}

	private Item item(Long id, String nome) {
		Item item = new Item(nome, "Descrição", TipoItem.FISICO, 10, null, null);
		item.setId(id);
		item.setDataCriacao(LocalDateTime.of(2026, 9, 22, 12, 0));
		return item;
	}
}
