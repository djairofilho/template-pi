package br.insper.templatepi.validator;

import br.insper.templatepi.entity.TipoItem;
import br.insper.templatepi.exception.ValidacaoItemException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ValidadorItemFactoryTest {

	@Test
	void deveSelecionarStrategyPeloTipo() {
		ValidadorItem fisico = new ValidadorItemFisico();
		ValidadorItem digital = new ValidadorItemDigital();
		ValidadorItem servico = new ValidadorItemServico();
		ValidadorItemFactory factory = new ValidadorItemFactory(List.of(fisico, digital, servico));

		assertThat(factory.obter(TipoItem.FISICO)).isSameAs(fisico);
		assertThat(factory.obter(TipoItem.DIGITAL)).isSameAs(digital);
		assertThat(factory.obter(TipoItem.SERVICO)).isSameAs(servico);
	}

	@Test
	void deveExigirTipo() {
		ValidadorItemFactory factory = new ValidadorItemFactory(List.of());

		assertThatThrownBy(() -> factory.obter(null))
				.isInstanceOf(ValidacaoItemException.class)
				.hasMessage("O tipo é obrigatório");
	}

	@Test
	void deveFalharQuandoNaoExisteStrategy() {
		ValidadorItemFactory factory = new ValidadorItemFactory(List.of());

		assertThatThrownBy(() -> factory.obter(TipoItem.FISICO))
				.isInstanceOf(ValidacaoItemException.class)
				.hasMessageContaining("Não existe validador");
	}
}
