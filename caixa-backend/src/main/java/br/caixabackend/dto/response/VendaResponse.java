package br.caixabackend.dto.response;

import br.caixabackend.enums.FormaPagamento;
import br.caixabackend.enums.StatusVenda;
import br.caixabackend.enums.TipoVenda;
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
public class VendaResponse {
    private Long id;
    private Integer numeroVendaEvento;
    private Long eventoId;
    private String eventoNome;
    private String operadorNome;
    private TipoVenda tipo;
    private StatusVenda status;
    private BigDecimal total;
    private String clienteFiado;
    private LocalDateTime criadoEm;
    private List<ItemVendaResponse> itens;
    private FormaPagamento formaPagamento;
}
