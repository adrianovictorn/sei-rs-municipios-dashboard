package br.gov.rs.seimunicipios.equipe.dto;

import jakarta.validation.constraints.NotBlank;

public record EquipeRequest(
        @NotBlank String nome
) {
}
