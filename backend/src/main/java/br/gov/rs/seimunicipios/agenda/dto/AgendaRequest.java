package br.gov.rs.seimunicipios.agenda.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AgendaRequest(
        Long tipoAgendaId,
        @NotBlank String titulo,
        @NotNull LocalDateTime dataHora,
        String local,
        String observacoes,
        Boolean realizada
) {
}
