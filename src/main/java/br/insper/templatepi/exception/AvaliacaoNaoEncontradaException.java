package br.insper.templatepi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AvaliacaoNaoEncontradaException extends RuntimeException {

	public AvaliacaoNaoEncontradaException(Long id) {
		super("Avaliação com ID " + id + " não encontrada");
	}
}
