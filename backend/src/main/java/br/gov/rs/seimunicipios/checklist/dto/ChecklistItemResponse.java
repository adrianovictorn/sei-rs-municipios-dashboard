package br.gov.rs.seimunicipios.checklist.dto;

import br.gov.rs.seimunicipios.checklist.ChecklistItem;

import java.time.LocalDate;

public record ChecklistItemResponse(
        Long id,
        String descricao,
        boolean concluido,
        LocalDate dataConclusao,
        Integer ordem,
        Long templateId,
        LocalDate dataInicio,
        LocalDate dataFim,
        Integer duracaoDias,
        Integer percentualPrevisto,
        String predecessoras
) {
    public static ChecklistItemResponse from(ChecklistItem item) {
        return new ChecklistItemResponse(
                item.getId(),
                item.getDescricaoEfetiva(),
                item.isConcluido(),
                item.getDataConclusao(),
                item.getOrdemEfetiva(),
                item.getTemplateId(),
                item.getDataInicio(),
                item.getDataFim(),
                item.getDuracaoEfetiva(),
                item.getPercentualPrevisto(),
                item.getPredecessoras()
        );
    }
}
