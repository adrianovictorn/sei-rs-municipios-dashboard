package br.gov.rs.seimunicipios.checklist.dto;

import java.time.LocalDate;

public record ChecklistItemUpdateRequest(
        String descricao,
        Boolean concluido,
        Integer ordem,
        LocalDate dataInicio,
        LocalDate dataFim,
        Integer duracaoDias,
        Integer percentualPrevisto,
        String predecessoras
) {
}
