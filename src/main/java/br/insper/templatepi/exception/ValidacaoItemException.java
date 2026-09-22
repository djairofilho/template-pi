package br.insper.templatepi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ValidacaoItemException extends RuntimeException {

	public ValidacaoItemException(String mensagem) {
		super(mensagem);
	}
}
