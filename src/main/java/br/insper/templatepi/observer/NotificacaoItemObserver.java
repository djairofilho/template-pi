package br.insper.templatepi.observer;

import br.insper.templatepi.entity.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NotificacaoItemObserver implements ItemObserver {

	private static final Logger LOGGER = LoggerFactory.getLogger(NotificacaoItemObserver.class);

	@Override
	public void atualizar(Item item, String evento) {
		LOGGER.info("Notificação simulada para o item {}: evento {}", item.getId(), evento);
	}
}
