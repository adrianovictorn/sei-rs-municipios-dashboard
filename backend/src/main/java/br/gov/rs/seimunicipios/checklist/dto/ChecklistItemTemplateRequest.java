package br.gov.rs.seimunicipios.checklist.dto;

import jakarta.validation.constraints.NotBlank;

public record ChecklistItemTemplateRequest(
        @NotBlank String descricao,
        Integer ordem,
        Integer duracaoDias
) {
}
