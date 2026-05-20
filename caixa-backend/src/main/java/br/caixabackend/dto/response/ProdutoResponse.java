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
public class ProdutoResponse {
    private Long id;
    private String nome;
    private Long categoriaId;
    private String categoriaNome;
    private String imagemUrl;
    private BigDecimal precoCusto;
    private BigDecimal precoVenda;
    private Integer estoqueAtual;
    private Integer estoqueMinimo;
    private Boolean estoqueBaixo;
    private Boolean ativo;
}
