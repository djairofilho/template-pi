package br.insper.templatepi.service;

import br.insper.templatepi.entity.Avaliacao;
import br.insper.templatepi.entity.TipoOperacao;
import br.insper.templatepi.exception.AvaliacaoNaoEncontradaException;
import br.insper.templatepi.observer.AvaliacaoObserver;
import br.insper.templatepi.repository.AvaliacaoRepository;
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
class AvaliacaoServiceTest {

	@Mock
	private AvaliacaoRepository avaliacaoRepository;

	@Mock
	private AvaliacaoObserver observer;

	private AvaliacaoService avaliacaoService;

	@BeforeEach
	void configurarService() {
		avaliacaoService = new AvaliacaoService(avaliacaoRepository, List.of(observer));
	}

	@Test
	void deveCriarAvaliacaoENotificarObservers() {
		Avaliacao avaliacao = new Avaliacao("  Maria  ", "  Excelente atendimento  ", 5);
		avaliacao.setId(999L);
		when(avaliacaoRepository.save(any(Avaliacao.class))).thenAnswer(invocation -> {
			Avaliacao salva = invocation.getArgument(0);
			salva.setId(1L);
			return salva;
		});

		Avaliacao resposta = avaliacaoService.criar(avaliacao);

		ArgumentCaptor<Avaliacao> captor = ArgumentCaptor.forClass(Avaliacao.class);
		verify(avaliacaoRepository).save(captor.capture());
		Avaliacao salva = captor.getValue();
		assertThat(salva.getId()).isEqualTo(1L);
		assertThat(salva.getAutor()).isEqualTo("Maria");
		assertThat(salva.getConteudo()).isEqualTo("Excelente atendimento");
		assertThat(salva.getNota()).isEqualTo(5);
		assertThat(resposta).isSameAs(salva);
		verify(observer).atualizar(salva, TipoOperacao.CREATE);
	}

	@Test
	void deveListarAvaliacoesDaMaisRecenteParaAMaisAntiga() {
		Avaliacao avaliacao = new Avaliacao("Ana", "Muito bom", 5);
		when(avaliacaoRepository.findAllByOrderByDataAvaliacaoDesc())
				.thenReturn(List.of(avaliacao));

		assertThat(avaliacaoService.listar()).containsExactly(avaliacao);

		verify(avaliacaoRepository).findAllByOrderByDataAvaliacaoDesc();
	}

	@Test
	void deveBuscarAvaliacaoPorId() {
		Avaliacao avaliacao = new Avaliacao("Ana", "Bom", 4);
		when(avaliacaoRepository.findById(1L)).thenReturn(Optional.of(avaliacao));

		assertThat(avaliacaoService.buscarPorId(1L)).isSameAs(avaliacao);
	}

	@Test
	void deveFalharAoBuscarAvaliacaoInexistente() {
		when(avaliacaoRepository.findById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> avaliacaoService.buscarPorId(99L))
				.isInstanceOf(AvaliacaoNaoEncontradaException.class)
				.hasMessage("Avaliação com ID 99 não encontrada");
	}

	@Test
	void deveExcluirAvaliacaoENotificarObservers() {
		Avaliacao avaliacao = new Avaliacao("Ana", "Bom", 4);
		avaliacao.setId(3L);
		when(avaliacaoRepository.findById(3L)).thenReturn(Optional.of(avaliacao));

		avaliacaoService.excluir(3L);

		verify(avaliacaoRepository).delete(avaliacao);
		verify(observer).atualizar(avaliacao, TipoOperacao.DELETE);
	}

	@Test
	void deveFalharAoExcluirAvaliacaoInexistente() {
		when(avaliacaoRepository.findById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> avaliacaoService.excluir(99L))
				.isInstanceOf(AvaliacaoNaoEncontradaException.class);
	}
}
