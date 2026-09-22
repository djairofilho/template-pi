package br.insper.templatepi.processor;

import br.insper.templatepi.entity.Item;
import br.insper.templatepi.entity.TipoItem;
import org.springframework.stereotype.Component;

@Component
public class ProcessadorItemFisico implements ProcessadorItem {

	@Override
	public TipoItem tipoSuportado() {
		return TipoItem.FISICO;
	}

	@Override
	public String processar(Item item) {
		return "Item físico separado para envio";
	}
}
