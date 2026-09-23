package br.insper.templatepi.observer;

import br.insper.templatepi.entity.Auditoria;
import br.insper.templatepi.entity.Avaliacao;
import br.insper.templatepi.entity.TipoOperacao;
import br.insper.templatepi.repository.AuditoriaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuditoriaAvaliacaoObserverTest {

	@Mock
	private AuditoriaRepository auditoriaRepository;

	@Test
	void devePersistirEventoDeAuditoria() {
		Avaliacao avaliacao = new Avaliacao("Maria", "Atendimento", 5);
		avaliacao.setId(10L);
		AuditoriaAvaliacaoObserver observer = new AuditoriaAvaliacaoObserver(auditoriaRepository);
		LocalDateTime inicio = LocalDateTime.now();

		observer.atualizar(avaliacao, TipoOperacao.CREATE);

		ArgumentCaptor<Auditoria> captor = ArgumentCaptor.forClass(Auditoria.class);
		verify(auditoriaRepository).save(captor.capture());
		Auditoria auditoria = captor.getValue();
		assertThat(auditoria.getAvaliacaoId()).isEqualTo(10L);
		assertThat(auditoria.getTipoOperacao()).isEqualTo(TipoOperacao.CREATE);
		assertThat(auditoria.getTimestamp()).isBetween(inicio, LocalDateTime.now());
	}
}
