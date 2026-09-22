package br.insper.templatepi.repository;

import br.insper.templatepi.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// TODO(PI): adapte os métodos derivados aos filtros pedidos no enunciado.
@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

	List<Item> findByDeletadoFalseOrderByNomeAsc();

	List<Item> findByDeletadoFalseAndNomeStartingWithIgnoreCaseOrderByNomeAsc(String nome);

	Optional<Item> findByIdAndDeletadoFalse(Long id);
}
