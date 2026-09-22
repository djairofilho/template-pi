package br.insper.templatepi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProcessamentoItemResponse {

	private final boolean sucesso;
	private final String mensagem;
	private final ItemResponse item;
}
