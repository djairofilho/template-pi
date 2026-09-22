package br.insper.templatepi.observer;

import br.insper.templatepi.entity.Item;
import br.insper.templatepi.entity.StatusItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AuditoriaItemObserver implements ItemObserver {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuditoriaItemObserver.class);

	@Override
	public void atualizar(Item item, StatusItem statusAnterior, StatusItem statusNovo) {
		LOGGER.info(
				"Item {} alterou o status de {} para {}",
				item.getId(),
				statusAnterior,
				statusNovo
		);
	}
}
