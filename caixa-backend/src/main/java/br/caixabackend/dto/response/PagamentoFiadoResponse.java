package br.caixabackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagamentoFiadoResponse {
    private Long id;
    private Long vendaId;
    private String clienteFiado;
    private BigDecimal valorVenda;
    private BigDecimal valorPago;
    private BigDecimal valorRestante;
    private Boolean quitado;
    private LocalDateTime pagoEm;
    private String observacao;
}
