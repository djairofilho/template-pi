package br.insper.templatepi.integration;

import br.insper.templatepi.entity.Item;
import br.insper.templatepi.entity.StatusItem;
import br.insper.templatepi.entity.TipoItem;
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
				  "tipo": "FISICO",
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
				.andExpect(jsonPath("$.tipo").value("FISICO"))
				.andExpect(jsonPath("$.quantidade").value(10))
				.andExpect(jsonPath("$.status").value("PENDENTE"))
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
				  "tipo": "FISICO",
				  "quantidade": 0
				}
				""";

		mockMvc.perform(post("/itens")
					.contentType(MediaType.APPLICATION_JSON)
					.content(body))
				.andExpect(status().isBadRequest());
	}

	@Test
	void deveAplicarValidacaoCondicional() throws Exception {
		String digitalSemUrl = """
				{
				  "nome": "Arquivo",
				  "descricao": "Item digital",
				  "tipo": "DIGITAL"
				}
				""";

		mockMvc.perform(post("/itens")
					.contentType(MediaType.APPLICATION_JSON)
					.content(digitalSemUrl))
				.andExpect(status().isBadRequest());
	}

	@Test
	void deveFiltrarPorInicioDoNomeESemMostrarDeletados() throws Exception {
		itemRepository.save(itemFisico("Item de exemplo", 10));
		itemRepository.save(itemFisico("Instrumento", 5));
		Item deletado = itemFisico("Item removido", 3);
		deletado.setDeletado(true);
		itemRepository.save(deletado);
		itemRepository.save(itemFisico("Produto", 8));

		mockMvc.perform(get("/itens").param("nome", "item"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].nome").value("Item de exemplo"));
	}

	@Test
	void deveListarSomenteItensNaoDeletadosEOrdenados() throws Exception {
		itemRepository.save(itemFisico("Zeta", 10));
		itemRepository.save(itemFisico("Alfa", 10));
		Item deletado = itemFisico("Beta", 10);
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
		Item salvo = itemRepository.save(itemFisico("Item removível", 10));

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

	@Test
	void deveProcessarItemComStrategyEAtualizarStatus() throws Exception {
		Item salvo = itemRepository.save(itemFisico("Item processável", 10));

		mockMvc.perform(post("/itens/{id}/processar", salvo.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.sucesso").value(true))
				.andExpect(jsonPath("$.mensagem").value("Item físico separado para envio"))
				.andExpect(jsonPath("$.item.status").value("PROCESSADO"))
				.andExpect(jsonPath("$.item.dataProcessamento").isNotEmpty());

		Item processado = itemRepository.findById(salvo.getId()).orElseThrow();
		assertThat(processado.getStatus()).isEqualTo(StatusItem.PROCESSADO);
		assertThat(processado.getDataProcessamento()).isNotNull();
	}

	private Item itemFisico(String nome, int quantidade) {
		return new Item(nome, "Descrição", TipoItem.FISICO, quantidade, null, null);
	}
}
