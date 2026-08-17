package br.gov.rs.seimunicipios.checklist.dto;

import br.gov.rs.seimunicipios.checklist.ChecklistItemTemplate;

public record ChecklistItemTemplateResponse(
        Long id,
        String descricao,
        Integer ordem,
        Integer duracaoDias
) {
    public static ChecklistItemTemplateResponse from(ChecklistItemTemplate item) {
        return new ChecklistItemTemplateResponse(
                item.getId(),
                item.getDescricao(),
                item.getOrdem(),
                item.getDuracaoDias()
        );
    }
}
