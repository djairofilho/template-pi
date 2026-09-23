package br.insper.templatepi.validator;

import br.insper.templatepi.dto.ItemRequest;
import br.insper.templatepi.entity.TipoItem;
import br.insper.templatepi.exception.ValidacaoItemException;
import org.springframework.stereotype.Component;

// TODO(PI): adapte esta Strategy às regras do tipo físico da prova.
@Component
public class ValidadorItemFisico implements ValidadorItem {

	@Override
	public TipoItem tipoSuportado() {
		return TipoItem.FISICO;
	}

	@Override
	public void validar(ItemRequest request) {
		if (request.getQuantidade() == null || request.getQuantidade() <= 0) {
			throw new ValidacaoItemException("A quantidade deve ser positiva para itens físicos");
		}
	}
}
