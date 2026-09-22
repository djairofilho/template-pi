package br.insper.templatepi.dto;

import br.insper.templatepi.entity.TipoItem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

	@NotNull(message = "O tipo é obrigatório")
	private TipoItem tipo;

	private Integer quantidade;

	private String urlAcesso;

	private Integer duracaoMinutos;
}
