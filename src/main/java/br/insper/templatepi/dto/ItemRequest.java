package br.insper.templatepi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// TODO(PI): ajuste os campos e as validações conforme as regras do enunciado.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequest {

	@NotBlank(message = "O nome é obrigatório")
	private String nome;

	@NotBlank(message = "A descrição é obrigatória")
	private String descricao;

	@NotNull(message = "A quantidade é obrigatória")
	@Positive(message = "A quantidade deve ser maior que zero")
	private Integer quantidade;
}
