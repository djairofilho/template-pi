package br.insper.templatepi.processor;

import br.insper.templatepi.entity.TipoItem;
import br.insper.templatepi.exception.ValidacaoItemException;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

// Factory: centraliza a seleção e mantém ItemService independente das implementações.
@Component
public class ProcessadorItemFactory {

	private final Map<TipoItem, ProcessadorItem> processadores;

	public ProcessadorItemFactory(List<ProcessadorItem> processadores) {
		this.processadores = new EnumMap<>(TipoItem.class);
		for (ProcessadorItem processador : processadores) {
			this.processadores.put(processador.tipoSuportado(), processador);
		}
	}

	public ProcessadorItem obter(TipoItem tipo) {
		ProcessadorItem processador = processadores.get(tipo);
		if (processador == null) {
			throw new ValidacaoItemException("Não existe processador para o tipo " + tipo);
		}
		return processador;
	}
}
