package br.insper.templatepi.validator;

import br.insper.templatepi.entity.TipoItem;
import br.insper.templatepi.exception.ValidacaoItemException;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

// Factory: seleciona a Strategy de validação correspondente ao tipo do item.
@Component
public class ValidadorItemFactory {

	private final Map<TipoItem, ValidadorItem> validadores;

	public ValidadorItemFactory(List<ValidadorItem> validadores) {
		this.validadores = new EnumMap<>(TipoItem.class);
		for (ValidadorItem validador : validadores) {
			this.validadores.put(validador.tipoSuportado(), validador);
		}
	}

	public ValidadorItem obter(TipoItem tipo) {
		if (tipo == null) {
			throw new ValidacaoItemException("O tipo é obrigatório");
		}

		ValidadorItem validador = validadores.get(tipo);
		if (validador == null) {
			throw new ValidacaoItemException("Não existe validador para o tipo " + tipo);
		}
		return validador;
	}
}
