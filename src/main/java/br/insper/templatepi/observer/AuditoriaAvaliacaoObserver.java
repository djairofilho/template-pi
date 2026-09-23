package br.insper.templatepi.observer;

import br.insper.templatepi.entity.Auditoria;
import br.insper.templatepi.entity.Avaliacao;
import br.insper.templatepi.entity.TipoOperacao;
import br.insper.templatepi.repository.AuditoriaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AuditoriaAvaliacaoObserver implements AvaliacaoObserver {

	private final AuditoriaRepository auditoriaRepository;

	public AuditoriaAvaliacaoObserver(AuditoriaRepository auditoriaRepository) {
		this.auditoriaRepository = auditoriaRepository;
	}

	@Override
	public void atualizar(Avaliacao avaliacao, TipoOperacao operacao) {
		Auditoria auditoria = new Auditoria(avaliacao.getId(), LocalDateTime.now(), operacao);
		auditoriaRepository.save(auditoria);
	}
}
