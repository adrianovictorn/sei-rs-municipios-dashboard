package br.gov.rs.seimunicipios.etapa.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record EtapaRequest(
        @NotBlank String nome,
        String descricao,
        Integer ordem,
        LocalDate dataSolicitacao,
        LocalDate dataInicio,
        LocalDate dataFim,
        Integer duracaoDias,
        Integer percentualPrevisto,
        String predecessoras
) {
}
