package br.caixabackend.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProdutoRequest {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotNull(message = "Categoria é obrigatória")
    private Long categoriaId;

    private String imagemUrl;

    @NotNull(message = "Preço de custo é obrigatório")
    @DecimalMin(value = "0.0", inclusive = false, message = "Preço de custo deve ser maior que zero")
    private BigDecimal precoCusto;

    @NotNull(message = "Preço de venda é obrigatório")
    @DecimalMin(value = "0.0", inclusive = false, message = "Preço de venda deve ser maior que zero")
    private BigDecimal precoVenda;

    @NotNull(message = "Estoque atual é obrigatório")
    @Min(value = 0, message = "Estoque não pode ser negativo")
    private Integer estoqueAtual;

    @NotNull(message = "Estoque mínimo é obrigatório")
    @Min(value = 0, message = "Estoque mínimo não pode ser negativo")
    private Integer estoqueMinimo;
}
