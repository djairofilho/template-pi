package br.insper.templatepi.validator;

import br.insper.templatepi.dto.ItemRequest;
import br.insper.templatepi.entity.TipoItem;

// Strategy: cada implementação valida um tipo de item.
public interface ValidadorItem {

	TipoItem tipoSuportado();

	void validar(ItemRequest request);
}
