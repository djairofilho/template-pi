package br.insper.templatepi.client;

import br.insper.templatepi.exception.UsuarioNaoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withResourceNotFound;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class UsuarioClientTest {

	private MockRestServiceServer server;
	private UsuarioClient usuarioClient;

	@BeforeEach
	void configurarClient() {
		RestClient.Builder builder = RestClient.builder();
		server = MockRestServiceServer.bindTo(builder).build();
		usuarioClient = new UsuarioClient(builder, "http://usuarios.test/users");
	}

	@Test
	void deveBuscarEmailDoUsuario() {
		server.expect(once(), requestTo("http://usuarios.test/users/cliente-1"))
				.andRespond(withSuccess("{\"email\":\"cliente@exemplo.com\"}", MediaType.APPLICATION_JSON));

		assertThat(usuarioClient.buscarEmail("cliente-1")).isEqualTo("cliente@exemplo.com");

		server.verify();
	}

	@Test
	void deveFalharQuandoUsuarioNaoExiste() {
		server.expect(once(), requestTo("http://usuarios.test/users/inexistente"))
				.andRespond(withResourceNotFound());

		assertThatThrownBy(() -> usuarioClient.buscarEmail("inexistente"))
				.isInstanceOf(UsuarioNaoEncontradoException.class);

		server.verify();
	}

	@Test
	void deveFalharQuandoRespostaNaoContemEmail() {
		server.expect(once(), requestTo("http://usuarios.test/users/sem-email"))
				.andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

		assertThatThrownBy(() -> usuarioClient.buscarEmail("sem-email"))
				.isInstanceOf(UsuarioNaoEncontradoException.class);

		server.verify();
	}
}
