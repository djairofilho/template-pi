package br.insper.templatepi.observer;

import br.insper.templatepi.entity.Item;
import br.insper.templatepi.entity.StatusItem;

public interface ItemObservable {

	void notificarObservadores(Item item, StatusItem statusAnterior);
}
