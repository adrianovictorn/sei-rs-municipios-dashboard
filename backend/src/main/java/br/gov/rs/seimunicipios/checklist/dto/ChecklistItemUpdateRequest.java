package br.gov.rs.seimunicipios.checklist.dto;

public record ChecklistItemUpdateRequest(
        String descricao,
        Boolean concluido,
        Integer ordem
) {
}
