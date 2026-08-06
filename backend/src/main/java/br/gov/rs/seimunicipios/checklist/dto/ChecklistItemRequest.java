package br.gov.rs.seimunicipios.checklist.dto;

import jakarta.validation.constraints.NotBlank;

public record ChecklistItemRequest(
        @NotBlank String descricao,
        Integer ordem
) {
}
