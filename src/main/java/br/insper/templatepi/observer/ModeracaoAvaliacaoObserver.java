package br.insper.templatepi.observer;

import br.insper.templatepi.entity.Avaliacao;
import br.insper.templatepi.entity.TipoOperacao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ModeracaoAvaliacaoObserver implements AvaliacaoObserver {

	private static final Logger LOGGER = LoggerFactory.getLogger(ModeracaoAvaliacaoObserver.class);

	@Override
	public void atualizar(Avaliacao avaliacao, TipoOperacao operacao) {
		if (operacao == TipoOperacao.CREATE && avaliacao.getNota() <= 2) {
			LOGGER.warn(
					"Avaliação negativa criada: id={}, autor={}, nota={}",
					avaliacao.getId(),
					avaliacao.getAutor(),
					avaliacao.getNota()
			);
		}
	}
}
