package br.insper.templatepi.validator;

import br.insper.templatepi.entity.Item;
import br.insper.templatepi.entity.TipoItem;
import br.insper.templatepi.exception.ValidacaoItemException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ValidadorItemStrategyTest {

	private final ValidadorItem fisico = new ValidadorItemFisico();
	private final ValidadorItem digital = new ValidadorItemDigital();
	private final ValidadorItem servico = new ValidadorItemServico();

	@Test
	void deveInformarTipoSuportadoPorCadaStrategy() {
		assertThat(fisico.tipoSuportado()).isEqualTo(TipoItem.FISICO);
		assertThat(digital.tipoSuportado()).isEqualTo(TipoItem.DIGITAL);
		assertThat(servico.tipoSuportado()).isEqualTo(TipoItem.SERVICO);
	}

	@ParameterizedTest
	@NullSource
	@ValueSource(ints = {0, -1})
	void deveExigirQuantidadePositivaParaItemFisico(Integer quantidade) {
		assertThatThrownBy(() -> fisico.validar(item(TipoItem.FISICO, quantidade, null, null)))
				.isInstanceOf(ValidacaoItemException.class)
				.hasMessageContaining("quantidade");
	}

	@Test
	void deveAceitarItemFisicoValido() {
		assertThatCode(() -> fisico.validar(item(TipoItem.FISICO, 1, null, null)))
				.doesNotThrowAnyException();
	}

	@ParameterizedTest
	@NullSource
	@ValueSource(strings = {"", "   "})
	void deveExigirUrlParaItemDigital(String url) {
		assertThatThrownBy(() -> digital.validar(item(TipoItem.DIGITAL, null, url, null)))
				.isInstanceOf(ValidacaoItemException.class)
				.hasMessageContaining("URL");
	}

	@Test
	void deveAceitarItemDigitalValido() {
		assertThatCode(() -> digital.validar(
				item(TipoItem.DIGITAL, null, "https://exemplo.com", null)))
				.doesNotThrowAnyException();
	}

	@ParameterizedTest
	@NullSource
	@ValueSource(ints = {0, -1})
	void deveExigirDuracaoPositivaParaServico(Integer duracao) {
		assertThatThrownBy(() -> servico.validar(item(TipoItem.SERVICO, null, null, duracao)))
				.isInstanceOf(ValidacaoItemException.class)
				.hasMessageContaining("duração");
	}

	@Test
	void deveAceitarServicoValido() {
		assertThatCode(() -> servico.validar(item(TipoItem.SERVICO, null, null, 60)))
				.doesNotThrowAnyException();
	}

	private Item item(TipoItem tipo, Integer quantidade, String url, Integer duracao) {
		return new Item(
				"Nome",
				tipo,
				"cliente-1",
				quantidade,
				url,
				duracao,
				BigDecimal.ONE
		);
	}
}
