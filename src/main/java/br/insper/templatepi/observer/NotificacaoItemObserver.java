package br.insper.templatepi.observer;

import br.insper.templatepi.entity.Item;
import br.insper.templatepi.entity.StatusItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NotificacaoItemObserver implements ItemObserver {

	private static final Logger LOGGER = LoggerFactory.getLogger(NotificacaoItemObserver.class);

	@Override
	public void atualizar(Item item, StatusItem statusAnterior, StatusItem statusNovo) {
		LOGGER.info("Notificação simulada para o item {}: status {}", item.getId(), statusNovo);
	}
}
