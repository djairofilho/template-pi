package br.insper.templatepi.processor;

import br.insper.templatepi.entity.Item;
import br.insper.templatepi.entity.TipoItem;
import org.springframework.stereotype.Component;

@Component
public class ProcessadorItemServico implements ProcessadorItem {

	@Override
	public TipoItem tipoSuportado() {
		return TipoItem.SERVICO;
	}

	@Override
	public String processar(Item item) {
		return "Serviço encaminhado para agendamento";
	}
}
