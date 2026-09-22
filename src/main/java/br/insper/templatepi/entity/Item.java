package br.insper.templatepi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

// TODO(PI): troque Item, a tabela e os campos pelo domínio pedido no enunciado.
@Entity
@Table(name = "itens")
@Getter
@Setter
@NoArgsConstructor
public class Item {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 150)
	private String nome;

	@Column(nullable = false, length = 1000)
	private String descricao;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private TipoItem tipo;

	private Integer quantidade;

	@Column(length = 500)
	private String urlAcesso;

	private Integer duracaoMinutos;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private StatusItem status;

	private LocalDateTime dataProcessamento;

	@Column(nullable = false)
	private boolean deletado;

	@Column(nullable = false, updatable = false)
	private LocalDateTime dataCriacao;

	public Item(
			String nome,
			String descricao,
			TipoItem tipo,
			Integer quantidade,
			String urlAcesso,
			Integer duracaoMinutos) {
		this.nome = nome;
		this.descricao = descricao;
		this.tipo = tipo;
		this.quantidade = quantidade;
		this.urlAcesso = urlAcesso;
		this.duracaoMinutos = duracaoMinutos;
		this.status = StatusItem.PENDENTE;
		this.deletado = false;
	}

	@PrePersist
	void preencherDataCriacao() {
		if (dataCriacao == null) {
			dataCriacao = LocalDateTime.now();
		}
	}
}
