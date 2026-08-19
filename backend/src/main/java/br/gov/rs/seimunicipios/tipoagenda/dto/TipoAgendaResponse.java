package br.gov.rs.seimunicipios.tipoagenda.dto;

import br.gov.rs.seimunicipios.tipoagenda.TipoAgenda;

public record TipoAgendaResponse(
        Long id,
        String nome
) {
    public static TipoAgendaResponse from(TipoAgenda tipoAgenda) {
        return new TipoAgendaResponse(tipoAgenda.getId(), tipoAgenda.getNome());
    }
}
