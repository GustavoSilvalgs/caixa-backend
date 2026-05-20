package br.caixabackend.dto.request;

import br.caixabackend.enums.TipoVenda;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class VendaRequest {

    @NotNull(message = "Tipo é obrigatório")
    private TipoVenda tipo;

    private String clienteFiado;

    @NotEmpty(message = "A venda deve ter pelo menos um item")
    private List<ItemVendaRequest> itens;
}
