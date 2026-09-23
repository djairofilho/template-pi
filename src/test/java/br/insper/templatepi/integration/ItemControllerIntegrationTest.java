package br.insper.templatepi.integration;

import br.insper.templatepi.client.UsuarioClient;
import br.insper.templatepi.entity.Item;
import br.insper.templatepi.entity.TipoItem;
import br.insper.templatepi.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// TODO(PI): adapte os cenários de integração aos endpoints e regras da prova.
@Testcontainers
@SpringBootTest(properties = "usuarios.api.url=http://usuarios.test")
@AutoConfigureMockMvc
class ItemControllerIntegrationTest {

	@Container
	@ServiceConnection
	static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:17-alpine");

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ItemRepository itemRepository;

	@MockitoBean
	private UsuarioClient usuarioClient;

	@BeforeEach
	void limparBanco() {
		itemRepository.deleteAll();
	}

	@Test
	void deveCriarItemComDadosCalculadosNoBackend() throws Exception {
		when(usuarioClient.buscarEmail("cliente-1")).thenReturn("cliente@exemplo.com");
		String body = """
				{
				  "id": 999,
				  "nome": "Item de exemplo",
				  "tipo": "FISICO",
				  "clienteId": "cliente-1",
				  "emailCliente": "falso@exemplo.com",
				  "quantidade": 10,
				  "precoUnitario": 2.50,
				  "valorTotal": 1.00
				}
				""";

		mockMvc.perform(post("/itens")
					.contentType(MediaType.APPLICATION_JSON)
					.content(body))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.nome").value("Item de exemplo"))
				.andExpect(jsonPath("$.emailCliente").value("cliente@exemplo.com"))
				.andExpect(jsonPath("$.valorTotal").value(25.0))
				.andExpect(jsonPath("$.dataCriacao").isNotEmpty());

		assertThat(itemRepository.findAll()).singleElement().satisfies(item -> {
			assertThat(item.getId()).isNotEqualTo(999L);
			assertThat(item.getEmailCliente()).isEqualTo("cliente@exemplo.com");
			assertThat(item.getValorTotal()).isEqualByComparingTo("25.00");
		});
	}

	@Test
	void deveRejeitarCamposObrigatoriosInvalidos() throws Exception {
		String body = """
				{
				  "nome": " ",
				  "tipo": "FISICO",
				  "clienteId": "",
				  "quantidade": 1,
				  "precoUnitario": 0
				}
				""";

		mockMvc.perform(post("/itens")
					.contentType(MediaType.APPLICATION_JSON)
					.content(body))
				.andExpect(status().isBadRequest());
	}

	@Test
	void deveAplicarStrategyDeValidacao() throws Exception {
		String digitalSemUrl = """
				{
				  "nome": "Arquivo",
				  "tipo": "DIGITAL",
				  "clienteId": "cliente-1",
				  "precoUnitario": 10
				}
				""";

		mockMvc.perform(post("/itens")
					.contentType(MediaType.APPLICATION_JSON)
					.content(digitalSemUrl))
				.andExpect(status().isBadRequest());
	}

	@Test
	void deveFiltrarItensPeloCliente() throws Exception {
		itemRepository.save(item("Item A", "cliente-1"));
		itemRepository.save(item("Item B", "cliente-2"));

		mockMvc.perform(get("/itens").param("clienteId", "cliente-1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].nome").value("Item A"));
	}

	@Test
	void deveListarTodosOsItens() throws Exception {
		itemRepository.save(item("Item A", "cliente-1"));
		itemRepository.save(item("Item B", "cliente-2"));

		mockMvc.perform(get("/itens"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)));
	}

	@Test
	void deveDeletarItemERejeitarSegundaTentativa() throws Exception {
		Item salvo = itemRepository.save(item("Item removível", "cliente-1"));

		mockMvc.perform(delete("/itens/{id}", salvo.getId()))
				.andExpect(status().isNoContent());

		assertThat(itemRepository.findById(salvo.getId())).isEmpty();

		mockMvc.perform(delete("/itens/{id}", salvo.getId()))
				.andExpect(status().isNotFound());
	}

	private Item item(String nome, String clienteId) {
		Item item = new Item(
				nome,
				TipoItem.FISICO,
				clienteId,
				1,
				null,
				null,
				BigDecimal.TEN
		);
		item.setEmailCliente(clienteId + "@exemplo.com");
		item.setValorTotal(BigDecimal.TEN);
		return item;
	}
}
