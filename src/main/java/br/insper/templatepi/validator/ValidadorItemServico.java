package br.insper.templatepi.validator;

import br.insper.templatepi.dto.ItemRequest;
import br.insper.templatepi.entity.TipoItem;
import br.insper.templatepi.exception.ValidacaoItemException;
import org.springframework.stereotype.Component;

// TODO(PI): adapte esta Strategy às regras do tipo serviço da prova.
@Component
public class ValidadorItemServico implements ValidadorItem {

	@Override
	public TipoItem tipoSuportado() {
		return TipoItem.SERVICO;
	}

	@Override
	public void validar(ItemRequest request) {
		if (request.getDuracaoMinutos() == null || request.getDuracaoMinutos() <= 0) {
			throw new ValidacaoItemException("A duração deve ser positiva para serviços");
		}
	}
}
