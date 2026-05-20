package br.caixabackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaRankingResponse {
    private Long categoriaId;
    private String categoriaNome;
    private Integer quantidadeVendida;
    private BigDecimal receitaGerada;
    private BigDecimal lucroGerado;
}
