package br.caixabackend.dto.response;

import br.caixabackend.enums.StatusEvento;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventoResponse {
    private Long id;
    private String nome;
    private LocalDate data;
    private StatusEvento status;
    private String criadoPorNome;
    private LocalDateTime abertoEm;
    private LocalDateTime fechadoEm;
}
