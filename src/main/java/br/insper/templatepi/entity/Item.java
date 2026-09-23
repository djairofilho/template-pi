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

	@NotBlank(message = "O nome é obrigatório")
	@Column(nullable = false, length = 150)
	private String nome;

	@NotNull(message = "O tipo é obrigatório")
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private TipoItem tipo;

	@NotBlank(message = "O ID do cliente é obrigatório")
	@Column(nullable = false, length = 100)
	private String clienteId;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@Column(nullable = false, length = 200)
	private String emailCliente;

	private Integer quantidade;

	@Column(length = 500)
	private String urlAcesso;

	private Integer duracaoMinutos;

	@NotNull(message = "O preço unitário é obrigatório")
	@DecimalMin(value = "0.0", inclusive = false, message = "O preço unitário deve ser positivo")
	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal precoUnitario;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal valorTotal;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@Column(nullable = false, updatable = false)
	private LocalDateTime dataCriacao;

	public Item(
			String nome,
			TipoItem tipo,
			String clienteId,
			Integer quantidade,
			String urlAcesso,
			Integer duracaoMinutos,
			BigDecimal precoUnitario) {
		this.nome = nome;
		this.tipo = tipo;
		this.clienteId = clienteId;
		this.quantidade = quantidade;
		this.urlAcesso = urlAcesso;
		this.duracaoMinutos = duracaoMinutos;
		this.precoUnitario = precoUnitario;
	}

	@PrePersist
	void preencherDataCriacao() {
		if (dataCriacao == null) {
			dataCriacao = LocalDateTime.now();
		}
	}
}
