package br.insper.templatepi.processor;

import br.insper.templatepi.entity.Item;
import br.insper.templatepi.entity.TipoItem;
import org.springframework.stereotype.Component;

@Component
public class ProcessadorItemDigital implements ProcessadorItem {

	@Override
	public TipoItem tipoSuportado() {
		return TipoItem.DIGITAL;
	}

	@Override
	public String processar(Item item) {
		return "Acesso digital liberado";
	}
}
