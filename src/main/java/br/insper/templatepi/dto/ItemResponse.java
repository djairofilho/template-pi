package br.insper.templatepi.dto;

import br.insper.templatepi.entity.Item;
import br.insper.templatepi.entity.StatusItem;
import br.insper.templatepi.entity.TipoItem;
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
	private final TipoItem tipo;
	private final Integer quantidade;
	private final String urlAcesso;
	private final Integer duracaoMinutos;
	private final StatusItem status;
	private final LocalDateTime dataCriacao;
	private final LocalDateTime dataProcessamento;

	public static ItemResponse fromEntity(Item item) {
		return new ItemResponse(
				item.getId(),
				item.getNome(),
				item.getDescricao(),
				item.getTipo(),
				item.getQuantidade(),
				item.getUrlAcesso(),
				item.getDuracaoMinutos(),
				item.getStatus(),
				item.getDataCriacao(),
				item.getDataProcessamento()
		);
	}
}
