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

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// TODO(PI): adapte os cenários de integração aos endpoints e regras da prova.
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
	void deveCriarItem() throws Exception {
		String body = """
				{
				  "nome": "Item de exemplo",
				  "descricao": "Descrição do item",
				  "quantidade": 10
				}
				""";

		mockMvc.perform(post("/itens")
					.contentType(MediaType.APPLICATION_JSON)
					.content(body))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.nome").value("Item de exemplo"))
				.andExpect(jsonPath("$.descricao").value("Descrição do item"))
				.andExpect(jsonPath("$.quantidade").value(10))
				.andExpect(jsonPath("$.dataCriacao").isNotEmpty());

		assertThat(itemRepository.findAll()).singleElement().satisfies(item -> {
			assertThat(item.isDeletado()).isFalse();
			assertThat(item.getDataCriacao()).isNotNull();
		});
	}

	@Test
	void deveRejeitarItemInvalido() throws Exception {
		String body = """
				{
				  "nome": " ",
				  "descricao": "",
				  "quantidade": 0
				}
				""";

		mockMvc.perform(post("/itens")
					.contentType(MediaType.APPLICATION_JSON)
					.content(body))
				.andExpect(status().isBadRequest());
	}

	@Test
	void deveFiltrarPorInicioDoNomeESemMostrarDeletados() throws Exception {
		itemRepository.save(new Item("Item de exemplo", "Descrição", 10));
		itemRepository.save(new Item("Instrumento", "Descrição", 5));
		Item deletado = new Item("Item removido", "Descrição", 3);
		deletado.setDeletado(true);
		itemRepository.save(deletado);
		itemRepository.save(new Item("Produto", "Descrição", 8));

		mockMvc.perform(get("/itens").param("nome", "item"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].nome").value("Item de exemplo"));
	}

	@Test
	void deveListarSomenteItensNaoDeletadosEOrdenados() throws Exception {
		itemRepository.save(new Item("Zeta", "Descrição", 10));
		itemRepository.save(new Item("Alfa", "Descrição", 10));
		Item deletado = new Item("Beta", "Descrição", 10);
		deletado.setDeletado(true);
		itemRepository.save(deletado);

		mockMvc.perform(get("/itens"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].nome").value("Alfa"))
				.andExpect(jsonPath("$[1].nome").value("Zeta"));
	}

	@Test
	void deveDeletarItemLogicamenteERejeitarSegundaTentativa() throws Exception {
		Item salvo = itemRepository.save(new Item("Item removível", "Descrição", 10));

		mockMvc.perform(delete("/itens/{id}", salvo.getId()))
				.andExpect(status().isNoContent());

		Item deletado = itemRepository.findById(salvo.getId()).orElseThrow();
		assertThat(deletado.isDeletado()).isTrue();

		mockMvc.perform(get("/itens"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(0)));

		mockMvc.perform(delete("/itens/{id}", salvo.getId()))
				.andExpect(status().isNotFound());
	}
}
