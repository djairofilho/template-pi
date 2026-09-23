package br.insper.templatepi.repository;

import br.insper.templatepi.entity.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {

	List<Avaliacao> findAllByOrderByDataAvaliacaoDesc();
}
