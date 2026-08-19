package br.gov.rs.seimunicipios.tipoagenda.dto;

import jakarta.validation.constraints.NotBlank;

public record TipoAgendaRequest(
        @NotBlank String nome
) {
}
