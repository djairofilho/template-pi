package br.insper.templatepi.validator;

import br.insper.templatepi.dto.ItemRequest;
import br.insper.templatepi.entity.TipoItem;
import br.insper.templatepi.exception.ValidacaoItemException;
import org.springframework.stereotype.Component;

// TODO(PI): adapte esta Strategy às regras do tipo digital da prova.
@Component
public class ValidadorItemDigital implements ValidadorItem {

	@Override
	public TipoItem tipoSuportado() {
		return TipoItem.DIGITAL;
	}

	@Override
	public void validar(ItemRequest request) {
		if (request.getUrlAcesso() == null || request.getUrlAcesso().isBlank()) {
			throw new ValidacaoItemException("A URL de acesso é obrigatória para itens digitais");
		}
	}
}
