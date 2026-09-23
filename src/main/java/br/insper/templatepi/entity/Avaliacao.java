package br.insper.templatepi.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "avaliacoes")
@Getter
@Setter
@NoArgsConstructor
public class Avaliacao {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Long id;

	@NotBlank(message = "O autor é obrigatório")
	@Column(nullable = false, length = 150)
	private String autor;

	@NotBlank(message = "O conteúdo é obrigatório")
	@Column(nullable = false, length = 1000)
	private String conteudo;

	@NotNull(message = "A nota é obrigatória")
	@Min(value = 1, message = "A nota deve ser no mínimo 1")
	@Max(value = 5, message = "A nota deve ser no máximo 5")
	@Column(nullable = false)
	private Integer nota;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@Column(nullable = false, updatable = false)
	private LocalDateTime dataAvaliacao;

	public Avaliacao(String autor, String conteudo, Integer nota) {
		this.autor = autor;
		this.conteudo = conteudo;
		this.nota = nota;
	}

	@PrePersist
	void preencherDataAvaliacao() {
		if (dataAvaliacao == null) {
			dataAvaliacao = LocalDateTime.now();
		}
	}
}
