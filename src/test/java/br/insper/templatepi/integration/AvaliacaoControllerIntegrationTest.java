package br.insper.templatepi.integration;

import br.insper.templatepi.entity.Avaliacao;
import br.insper.templatepi.entity.TipoOperacao;
import br.insper.templatepi.repository.AuditoriaRepository;
import br.insper.templatepi.repository.AvaliacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class AvaliacaoControllerIntegrationTest {

	@Container
	@ServiceConnection
	static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:17-alpine");

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private AvaliacaoRepository avaliacaoRepository;

	@Autowired
	private AuditoriaRepository auditoriaRepository;

	@BeforeEach
	void limparBanco() {
		auditoriaRepository.deleteAll();
		avaliacaoRepository.deleteAll();
	}

	@Test
	void deveCriarAvaliacaoEPersistirAuditoria() throws Exception {
		String body = """
				{
				  "id": 999,
				  "autor": "  Maria  ",
				  "conteudo": "  Excelente atendimento  ",
				  "nota": 5,
				  "dataAvaliacao": "2000-01-01T00:00:00"
				}
				""";

		mockMvc.perform(post("/avaliacoes")
					.contentType(MediaType.APPLICATION_JSON)
					.content(body))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.autor").value("Maria"))
				.andExpect(jsonPath("$.conteudo").value("Excelente atendimento"))
				.andExpect(jsonPath("$.nota").value(5))
				.andExpect(jsonPath("$.dataAvaliacao").isNotEmpty());

		Avaliacao salva = avaliacaoRepository.findAll().getFirst();
		assertThat(salva.getId()).isNotEqualTo(999L);
		assertThat(salva.getDataAvaliacao()).isAfter(LocalDateTime.of(2000, 1, 1, 0, 0));
		assertThat(auditoriaRepository.findAll()).singleElement().satisfies(auditoria -> {
			assertThat(auditoria.getAvaliacaoId()).isEqualTo(salva.getId());
			assertThat(auditoria.getTipoOperacao()).isEqualTo(TipoOperacao.CREATE);
			assertThat(auditoria.getTimestamp()).isNotNull();
		});
	}

	@Test
	void deveRejeitarAvaliacaoInvalida() throws Exception {
		String body = """
				{
				  "autor": " ",
				  "conteudo": "",
				  "nota": 6
				}
				""";

		mockMvc.perform(post("/avaliacoes")
					.contentType(MediaType.APPLICATION_JSON)
					.content(body))
				.andExpect(status().isBadRequest());
	}

	@Test
	void deveListarAvaliacoesDaMaisRecenteParaAMaisAntiga() throws Exception {
		Avaliacao antiga = new Avaliacao("Ana", "Bom", 4);
		antiga.setDataAvaliacao(LocalDateTime.of(2026, 9, 22, 10, 0));
		antiga = avaliacaoRepository.save(antiga);
		Avaliacao recente = new Avaliacao("Bruno", "Excelente", 5);
		recente.setDataAvaliacao(LocalDateTime.of(2026, 9, 23, 10, 0));
		recente = avaliacaoRepository.save(recente);

		mockMvc.perform(get("/avaliacoes"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].id").value(recente.getId()))
				.andExpect(jsonPath("$[1].id").value(antiga.getId()));
	}

	@Test
	void deveBuscarAvaliacaoPorId() throws Exception {
		Avaliacao salva = avaliacaoRepository.save(new Avaliacao("Ana", "Bom", 4));

		mockMvc.perform(get("/avaliacoes/{id}", salva.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(salva.getId()))
				.andExpect(jsonPath("$.autor").value("Ana"));

		mockMvc.perform(get("/avaliacoes/{id}", Long.MAX_VALUE))
				.andExpect(status().isNotFound());
	}

	@Test
	void deveExcluirAvaliacaoEPersistirAuditoria() throws Exception {
		Avaliacao salva = avaliacaoRepository.save(new Avaliacao("Ana", "Removível", 3));

		mockMvc.perform(delete("/avaliacoes/{id}", salva.getId()))
				.andExpect(status().isNoContent());

		assertThat(avaliacaoRepository.findById(salva.getId())).isEmpty();
		assertThat(auditoriaRepository.findAll()).singleElement().satisfies(auditoria -> {
			assertThat(auditoria.getAvaliacaoId()).isEqualTo(salva.getId());
			assertThat(auditoria.getTipoOperacao()).isEqualTo(TipoOperacao.DELETE);
		});

		mockMvc.perform(delete("/avaliacoes/{id}", salva.getId()))
				.andExpect(status().isNotFound());
	}
}
