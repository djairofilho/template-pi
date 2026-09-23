package br.insper.templatepi.integration;

import br.insper.templatepi.entity.Item;
import br.insper.templatepi.repository.ItemRepository;
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
class ItemControllerIntegrationTest {

	@Container
	@ServiceConnection
	static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:17-alpine");

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ItemRepository itemRepository;

	@BeforeEach
	void limparBanco() {
		itemRepository.deleteAll();
	}

	@Test
	void deveCriarAvaliacao() throws Exception {
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

		assertThat(itemRepository.findAll()).singleElement().satisfies(item -> {
			assertThat(item.getId()).isNotEqualTo(999L);
			assertThat(item.getDataAvaliacao()).isAfter(LocalDateTime.of(2000, 1, 1, 0, 0));
		});
	}

	@Test
	void deveRejeitarCamposObrigatoriosInvalidos() throws Exception {
		String body = """
				{
				  "autor": " ",
				  "conteudo": "",
				  "nota": null
				}
				""";

		mockMvc.perform(post("/avaliacoes")
					.contentType(MediaType.APPLICATION_JSON)
					.content(body))
				.andExpect(status().isBadRequest());
	}

	@Test
	void deveRejeitarNotaForaDoIntervalo() throws Exception {
		String body = """
				{
				  "autor": "Maria",
				  "conteudo": "Avaliação",
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
		Item antiga = new Item("Ana", "Bom", 4);
		antiga.setDataAvaliacao(LocalDateTime.of(2026, 9, 22, 10, 0));
		antiga = itemRepository.save(antiga);
		Item recente = new Item("Bruno", "Excelente", 5);
		recente.setDataAvaliacao(LocalDateTime.of(2026, 9, 23, 10, 0));
		recente = itemRepository.save(recente);

		mockMvc.perform(get("/avaliacoes"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].id").value(recente.getId()))
				.andExpect(jsonPath("$[1].id").value(antiga.getId()));
	}

	@Test
	void deveDeletarAvaliacaoERejeitarSegundaTentativa() throws Exception {
		Item salvo = itemRepository.save(new Item("Ana", "Removível", 3));

		mockMvc.perform(delete("/avaliacoes/{id}", salvo.getId()))
				.andExpect(status().isNoContent());

		assertThat(itemRepository.findById(salvo.getId())).isEmpty();

		mockMvc.perform(delete("/avaliacoes/{id}", salvo.getId()))
				.andExpect(status().isNotFound());
	}
}
