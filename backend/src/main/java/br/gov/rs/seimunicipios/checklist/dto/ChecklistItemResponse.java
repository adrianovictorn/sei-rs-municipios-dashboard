package br.gov.rs.seimunicipios.checklist.dto;

import br.gov.rs.seimunicipios.checklist.ChecklistItem;

import java.time.LocalDate;

public record ChecklistItemResponse(
        Long id,
        String descricao,
        boolean concluido,
        LocalDate dataConclusao,
        Integer ordem
) {
    public static ChecklistItemResponse from(ChecklistItem item) {
        return new ChecklistItemResponse(
                item.getId(),
                item.getDescricao(),
                item.isConcluido(),
                item.getDataConclusao(),
                item.getOrdem()
        );
    }
}
