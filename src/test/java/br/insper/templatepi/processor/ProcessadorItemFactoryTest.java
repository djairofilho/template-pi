package br.insper.templatepi.processor;

import br.insper.templatepi.entity.TipoItem;
import br.insper.templatepi.exception.ValidacaoItemException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProcessadorItemFactoryTest {

	@Test
	void deveSelecionarStrategyPeloTipo() {
		ProcessadorItem fisico = new ProcessadorItemFisico();
		ProcessadorItem digital = new ProcessadorItemDigital();
		ProcessadorItem servico = new ProcessadorItemServico();
		ProcessadorItemFactory factory = new ProcessadorItemFactory(List.of(fisico, digital, servico));

		assertThat(factory.obter(TipoItem.FISICO)).isSameAs(fisico);
		assertThat(factory.obter(TipoItem.DIGITAL)).isSameAs(digital);
		assertThat(factory.obter(TipoItem.SERVICO)).isSameAs(servico);
	}

	@Test
	void deveFalharQuandoNaoExisteStrategy() {
		ProcessadorItemFactory factory = new ProcessadorItemFactory(List.of());

		assertThatThrownBy(() -> factory.obter(TipoItem.FISICO))
				.isInstanceOf(ValidacaoItemException.class)
				.hasMessageContaining("FISICO");
	}
}
