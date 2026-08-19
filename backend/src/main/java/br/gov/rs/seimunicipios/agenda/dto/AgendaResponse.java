package br.gov.rs.seimunicipios.agenda.dto;

import br.gov.rs.seimunicipios.agenda.Agenda;

import java.time.LocalDateTime;

public record AgendaResponse(
        Long id,
        Long municipioId,
        String municipioNome,
        Long tipoAgendaId,
        String tipoAgendaNome,
        String titulo,
        LocalDateTime dataHora,
        String local,
        String observacoes,
        boolean realizada
) {
    public static AgendaResponse from(Agenda agenda) {
        return new AgendaResponse(
                agenda.getId(),
                agenda.getMunicipio().getId(),
                agenda.getMunicipio().getNome(),
                agenda.getTipoAgenda() != null ? agenda.getTipoAgenda().getId() : null,
                agenda.getTipoAgenda() != null ? agenda.getTipoAgenda().getNome() : null,
                agenda.getTitulo(),
                agenda.getDataHora(),
                agenda.getLocal(),
                agenda.getObservacoes(),
                agenda.isRealizada()
        );
    }
}
