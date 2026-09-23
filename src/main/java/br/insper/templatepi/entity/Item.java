package br.insper.templatepi.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.math.BigDecimal;

// TODO(PI): troque Item, a tabela e os campos pelo domínio pedido no enunciado.
@Entity
@Table(name = "itens")
@Getter
@Setter
@NoArgsConstructor
public class Item {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Long id;

	@NotBlank(message = "O autor é obrigatório")
	@Column(nullable = false, length = 150)
	private String autor;

	@NotBlank(message = "O autor é obrigatório")
	@Column(nullable = false, length = 150)
	private String conteudo;

	@NotBlank(message = "A nota é obrigatório")
	@Column(nullable = false, length = 150)
	private Integer nota;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@Column(nullable = false, updatable = false)
	private LocalDateTime dataAvaliacao;

	public Item(
			String autor,
			String conteudo,
			Integer nota,
			LocalDateTime dataAvaliacao) {

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
