package br.insper.templatepi.dto;

import br.insper.templatepi.entity.Item;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

// TODO(PI): exponha somente os campos que devem aparecer na resposta da API.
@Getter
@AllArgsConstructor
public class ItemResponse {

	private final Long id;
	private final String nome;
	private final String descricao;
	private final Integer quantidade;
	private final LocalDateTime dataCriacao;

	public static ItemResponse fromEntity(Item item) {
		return new ItemResponse(
				item.getId(),
				item.getNome(),
				item.getDescricao(),
				item.getQuantidade(),
				item.getDataCriacao()
		);
	}
}
