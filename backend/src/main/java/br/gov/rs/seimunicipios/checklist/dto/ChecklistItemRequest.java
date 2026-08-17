package br.gov.rs.seimunicipios.checklist.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record ChecklistItemRequest(
        @NotBlank String descricao,
        Integer ordem,
        LocalDate dataInicio,
        LocalDate dataFim,
        Integer duracaoDias,
        Integer percentualPrevisto,
        String predecessoras
) {
}
