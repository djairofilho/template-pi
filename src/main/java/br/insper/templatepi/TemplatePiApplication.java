package br.insper.templatepi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

// TODO(PI): renomeie a aplicação e o pacote-base se o enunciado exigir.
@SpringBootApplication
public class TemplatePiApplication {

	public static void main(String[] args) {
		SpringApplication.run(TemplatePiApplication.class, args);
	}

	@Bean
	RestClient.Builder restClientBuilder() {
		return RestClient.builder();
	}
}
