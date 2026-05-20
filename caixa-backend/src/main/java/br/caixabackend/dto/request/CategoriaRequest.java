package br.caixabackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoriaRequest {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;
}