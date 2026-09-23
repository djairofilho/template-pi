package br.insper.templatepi.client;

import br.insper.templatepi.exception.UsuarioNaoEncontradoException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;

@Component
public class UsuarioClient {

	private final RestClient restClient;

	public UsuarioClient(
			RestClient.Builder builder,
			@Value("${usuarios.api.url}") String usuariosApiUrl) {
		this.restClient = builder.baseUrl(usuariosApiUrl).build();
	}

	public String buscarEmail(String clienteId) {
		try {
			JsonNode usuario = restClient.get()
					.uri("/{id}", clienteId)
					.retrieve()
					.body(JsonNode.class);

			String email = usuario == null ? "" : usuario.path("email").asString("");
			if (email.isBlank()) {
				throw new UsuarioNaoEncontradoException(clienteId);
			}
			return email;
		} catch (HttpClientErrorException.NotFound exception) {
			throw new UsuarioNaoEncontradoException(clienteId);
		}
	}
}
