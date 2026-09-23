package br.insper.templatepi.repository;

import br.insper.templatepi.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// TODO(PI): adapte os métodos derivados aos filtros pedidos no enunciado.
@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

	List<Item> findAllByOrderByDataCriacaoDesc();

}
