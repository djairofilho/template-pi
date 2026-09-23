package br.insper.templatepi.observer;

import br.insper.templatepi.entity.Avaliacao;
import br.insper.templatepi.entity.TipoOperacao;

public interface AvaliacaoObserver {

	void atualizar(Avaliacao avaliacao, TipoOperacao operacao);
}
