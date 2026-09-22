package br.insper.templatepi.validator;

import br.insper.templatepi.dto.ItemRequest;
import br.insper.templatepi.exception.ValidacaoItemException;
import org.springframework.stereotype.Component;

// TODO(PI): mantenha aqui as regras que dependem de mais de um campo.
@Component
public class ValidadorItem {

	public void validar(ItemRequest request) {
		if (request.getTipo() == null) {
			throw new ValidacaoItemException("O tipo é obrigatório");
		}

		switch (request.getTipo()) {
			case FISICO -> validarFisico(request);
			case DIGITAL -> validarDigital(request);
			case SERVICO -> validarServico(request);
		}
	}

	private void validarFisico(ItemRequest request) {
		if (request.getQuantidade() == null || request.getQuantidade() <= 0) {
			throw new ValidacaoItemException("A quantidade deve ser positiva para itens físicos");
		}
	}

	private void validarDigital(ItemRequest request) {
		if (request.getUrlAcesso() == null || request.getUrlAcesso().isBlank()) {
			throw new ValidacaoItemException("A URL de acesso é obrigatória para itens digitais");
		}
	}

	private void validarServico(ItemRequest request) {
		if (request.getDuracaoMinutos() == null || request.getDuracaoMinutos() <= 0) {
			throw new ValidacaoItemException("A duração deve ser positiva para serviços");
		}
	}
}
