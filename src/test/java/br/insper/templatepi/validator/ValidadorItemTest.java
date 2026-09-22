package br.insper.templatepi.validator;

import br.insper.templatepi.dto.ItemRequest;
import br.insper.templatepi.entity.TipoItem;
import br.insper.templatepi.exception.ValidacaoItemException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ValidadorItemTest {

	private final ValidadorItem validador = new ValidadorItem();

	@Test
	void deveExigirTipo() {
		ItemRequest request = request(null, null, null, null);

		assertThatThrownBy(() -> validador.validar(request))
				.isInstanceOf(ValidacaoItemException.class)
				.hasMessage("O tipo é obrigatório");
	}

	@ParameterizedTest
	@NullSource
	@ValueSource(ints = {0, -1})
	void deveExigirQuantidadePositivaParaItemFisico(Integer quantidade) {
		ItemRequest request = request(TipoItem.FISICO, quantidade, null, null);

		assertThatThrownBy(() -> validador.validar(request))
				.isInstanceOf(ValidacaoItemException.class)
				.hasMessageContaining("quantidade");
	}

	@Test
	void deveAceitarItemFisicoValido() {
		assertThatCode(() -> validador.validar(request(TipoItem.FISICO, 1, null, null)))
				.doesNotThrowAnyException();
	}

	@ParameterizedTest
	@NullSource
	@ValueSource(strings = {"", "   "})
	void deveExigirUrlParaItemDigital(String url) {
		ItemRequest request = request(TipoItem.DIGITAL, null, url, null);

		assertThatThrownBy(() -> validador.validar(request))
				.isInstanceOf(ValidacaoItemException.class)
				.hasMessageContaining("URL");
	}

	@Test
	void deveAceitarItemDigitalValido() {
		assertThatCode(() -> validador.validar(
				request(TipoItem.DIGITAL, null, "https://exemplo.com", null)))
				.doesNotThrowAnyException();
	}

	@ParameterizedTest
	@NullSource
	@ValueSource(ints = {0, -1})
	void deveExigirDuracaoPositivaParaServico(Integer duracao) {
		ItemRequest request = request(TipoItem.SERVICO, null, null, duracao);

		assertThatThrownBy(() -> validador.validar(request))
				.isInstanceOf(ValidacaoItemException.class)
				.hasMessageContaining("duração");
	}

	@Test
	void deveAceitarServicoValido() {
		assertThatCode(() -> validador.validar(request(TipoItem.SERVICO, null, null, 60)))
				.doesNotThrowAnyException();
	}

	private ItemRequest request(
			TipoItem tipo,
			Integer quantidade,
			String url,
			Integer duracao) {
		return new ItemRequest("Nome", "Descrição", tipo, quantidade, url, duracao);
	}
}
