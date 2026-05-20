package br.caixabackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FiadoResumoResponse {
    private Long vendaId;
    private String clienteFiado;
    private BigDecimal valorTotal;
    private BigDecimal totalPago;
    private BigDecimal totalRestante;
    private Boolean quitado;
    private LocalDateTime criadoEm;
    private List<PagamentoFiadoResponse> pagamentos;
}