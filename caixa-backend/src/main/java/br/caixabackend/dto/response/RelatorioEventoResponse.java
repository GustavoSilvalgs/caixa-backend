package br.caixabackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelatorioEventoResponse {
    private Long eventoId;
    private String eventoNome;
    private LocalDate eventoData;

    // Resumo geral
    private Integer totalVendas;
    private Integer totalVendasNormais;
    private Integer totalVendaFiado;
    private BigDecimal receitaTotal;
    private BigDecimal receitaRecebida;
    private BigDecimal receitaPendenteFiado;
    private BigDecimal lucroTotal;

    // Rankings
    private List<ProdutoRankingResponse> produtosMaisVendidos;
    private List<ProdutoRankingResponse> produtosMaisLucrativos;
    private List<CategoriaRankingResponse> categorias;

    private Map<String, BigDecimal> receitaPorFormaPagamento;

    // Operadores
    private List<OperadorResumoResponse> resumoPorOperador;
}
