package br.insper.templatepi.processor;

import br.insper.templatepi.entity.Item;
import br.insper.templatepi.entity.TipoItem;

// Strategy: cada implementação processa um tipo sem criar condicionais no serviço.
public interface ProcessadorItem {

	TipoItem tipoSuportado();

	String processar(Item item);
}
