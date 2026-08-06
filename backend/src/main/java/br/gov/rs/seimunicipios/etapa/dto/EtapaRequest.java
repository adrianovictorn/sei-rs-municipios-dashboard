package br.gov.rs.seimunicipios.etapa.dto;

import jakarta.validation.constraints.NotBlank;

public record EtapaRequest(
        @NotBlank String nome,
        String descricao,
        Integer ordem
) {
}
