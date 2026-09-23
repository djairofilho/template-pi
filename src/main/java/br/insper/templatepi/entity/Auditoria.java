package br.insper.templatepi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "auditorias")
@Getter
@NoArgsConstructor
public class Auditoria {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long avaliacaoId;

	@Column(nullable = false, updatable = false)
	private LocalDateTime timestamp;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 10, updatable = false)
	private TipoOperacao tipoOperacao;

	public Auditoria(Long avaliacaoId, LocalDateTime timestamp, TipoOperacao tipoOperacao) {
		this.avaliacaoId = avaliacaoId;
		this.timestamp = timestamp;
		this.tipoOperacao = tipoOperacao;
	}
}
