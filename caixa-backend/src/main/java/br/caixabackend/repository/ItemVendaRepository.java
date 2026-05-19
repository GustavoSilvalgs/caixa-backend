package br.caixabackend.repository;

import br.caixabackend.entity.ItemVenda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ItemVendaRepository extends JpaRepository<ItemVenda, Long> {

    @Query("""
        SELECT i FROM ItemVenda i
        WHERE i.venda.evento.id = :eventoId
        ORDER BY i.subtotal DESC
    """)
    List<ItemVenda> findAllByEventoId(@Param("eventoId") Long eventoId);
}
