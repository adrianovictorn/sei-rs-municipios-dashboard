package br.gov.rs.seimunicipios.etapa.dto;

import jakarta.validation.constraints.NotBlank;

public record EtapaTemplateRequest(
        @NotBlank String nome,
        String descricao,
        Integer ordem,
        Integer duracaoDias
) {
}
