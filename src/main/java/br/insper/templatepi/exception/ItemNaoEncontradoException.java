package br.insper.templatepi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// TODO(PI): ajuste a exceção e o status HTTP conforme o contrato da prova.
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ItemNaoEncontradoException extends RuntimeException {

	public ItemNaoEncontradoException(Long id) {
		super("Item com ID " + id + " não encontrado");
	}
}
