package br.caixabackend.dto.response;

import br.caixabackend.enums.PerfilUsuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponse {
    private Long id;
    private String nome;
    private String email;
    private PerfilUsuario perfil;
    private Boolean ativo;
    private LocalDateTime criadoEm;
}
