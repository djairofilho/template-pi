package br.insper.templatepi.observer;

import br.insper.templatepi.entity.Item;

public interface ItemObserver {

	void atualizar(Item item, String evento);
}
