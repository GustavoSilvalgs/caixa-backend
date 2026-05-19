package br.caixabackend.repository;

import br.caixabackend.entity.PagamentoFiado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;

public interface PagamentoFiadoRepository extends JpaRepository<PagamentoFiado, Long> {
    List<PagamentoFiado> findAllByVendaId(Long vendaId);

    @Query("SELECT COALESCE(SUM(p.valor), 0) FROM PagamentoFiado p WHERE p.venda.id = :vendaId")
    BigDecimal sumValorByVendaId(@Param("vendaId") Long vendaId);
}
