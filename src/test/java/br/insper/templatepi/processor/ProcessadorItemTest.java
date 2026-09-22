package br.insper.templatepi.processor;

import br.insper.templatepi.entity.Item;
import br.insper.templatepi.entity.TipoItem;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProcessadorItemTest {

	@Test
	void deveExecutarAsStrategiesDeExemplo() {
		Item item = new Item("Nome", "Descrição", TipoItem.FISICO, 1, null, null);
		ProcessadorItem fisico = new ProcessadorItemFisico();
		ProcessadorItem digital = new ProcessadorItemDigital();
		ProcessadorItem servico = new ProcessadorItemServico();

		assertThat(fisico.tipoSuportado()).isEqualTo(TipoItem.FISICO);
		assertThat(fisico.processar(item)).contains("físico");
		assertThat(digital.tipoSuportado()).isEqualTo(TipoItem.DIGITAL);
		assertThat(digital.processar(item)).contains("digital");
		assertThat(servico.tipoSuportado()).isEqualTo(TipoItem.SERVICO);
		assertThat(servico.processar(item)).contains("Serviço");
	}
}
