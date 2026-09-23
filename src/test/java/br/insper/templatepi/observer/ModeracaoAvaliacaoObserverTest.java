package br.insper.templatepi.observer;

import br.insper.templatepi.entity.Avaliacao;
import br.insper.templatepi.entity.TipoOperacao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(OutputCaptureExtension.class)
class ModeracaoAvaliacaoObserverTest {

	private final ModeracaoAvaliacaoObserver observer = new ModeracaoAvaliacaoObserver();

	@ParameterizedTest
	@ValueSource(ints = {1, 2})
	void deveRegistrarMensagemParaAvaliacaoNegativa(int nota, CapturedOutput output) {
		Avaliacao avaliacao = new Avaliacao("Maria", "Ruim", nota);
		avaliacao.setId(1L);

		observer.atualizar(avaliacao, TipoOperacao.CREATE);

		assertThat(output).contains("Avaliação negativa criada")
				.contains("nota=" + nota);
	}

	@ParameterizedTest
	@ValueSource(ints = {3, 5})
	void naoDeveRegistrarMensagemParaAvaliacaoPositiva(int nota, CapturedOutput output) {
		observer.atualizar(new Avaliacao("Maria", "Bom", nota), TipoOperacao.CREATE);

		assertThat(output).doesNotContain("Avaliação negativa criada");
	}

	@Test
	void naoDeveModerarEventoDeExclusao(CapturedOutput output) {
		observer.atualizar(new Avaliacao("Maria", "Ruim", 1), TipoOperacao.DELETE);

		assertThat(output).doesNotContain("Avaliação negativa criada");
	}
}
