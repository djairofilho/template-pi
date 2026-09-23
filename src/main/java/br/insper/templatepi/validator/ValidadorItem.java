package br.insper.templatepi.validator;

import br.insper.templatepi.entity.Item;
import br.insper.templatepi.entity.TipoItem;

// Strategy: cada implementação valida um tipo de item.
public interface ValidadorItem {

	TipoItem tipoSuportado();

	void validar(Item item);
}
