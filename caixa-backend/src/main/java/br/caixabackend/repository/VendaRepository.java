package br.caixabackend.repository;

import br.caixabackend.entity.Venda;
import br.caixabackend.enums.TipoVenda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VendaRepository extends JpaRepository<Venda, Long> {
    List<Venda> findAllByEventoId(Long eventoId);
    List<Venda> findAllByEventoIdAndTipo(Long eventoId, TipoVenda tipo);

    @Query("SELECT v FROM Venda v WHERE v.evento.id = :eventoId AND v.tipo = 'FIADO' AND v.clienteFiado ILIKE %:nome%")
    List<Venda> findFiadosByEventoIdAndCliente(@Param("eventoId") Long eventoId, @Param("nome") String nome);
}
