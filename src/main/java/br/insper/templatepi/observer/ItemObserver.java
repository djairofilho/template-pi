package br.insper.templatepi.observer;

import br.insper.templatepi.entity.Item;
import br.insper.templatepi.entity.StatusItem;

public interface ItemObserver {

	void atualizar(Item item, StatusItem statusAnterior, StatusItem statusNovo);
}
