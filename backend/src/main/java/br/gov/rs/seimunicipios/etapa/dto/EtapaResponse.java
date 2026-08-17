package br.gov.rs.seimunicipios.etapa.dto;

import br.gov.rs.seimunicipios.checklist.dto.ChecklistItemResponse;
import br.gov.rs.seimunicipios.etapa.Etapa;
import br.gov.rs.seimunicipios.etapa.EtapaStatus;

import java.time.LocalDate;
import java.util.List;

public record EtapaResponse(
        Long id,
        String nome,
        String descricao,
        Integer ordem,
        int progresso,
        EtapaStatus status,
        Long templateId,
        LocalDate dataInicio,
        LocalDate dataFim,
        Integer duracaoDias,
        Integer percentualPrevisto,
        String predecessoras,
        List<ChecklistItemResponse> checklistItems
) {
    public static EtapaResponse from(Etapa etapa) {
        return new EtapaResponse(
                etapa.getId(),
                etapa.getNomeEfetivo(),
                etapa.getDescricaoEfetiva(),
                etapa.getOrdemEfetiva(),
                etapa.getProgresso(),
                etapa.getStatus(),
                etapa.getTemplateId(),
                etapa.getDataInicio(),
                etapa.getDataFim(),
                etapa.getDuracaoEfetiva(),
                etapa.getPercentualPrevisto(),
                etapa.getPredecessoras(),
                etapa.getChecklistItemsOrdenados().stream()
                        .map(ChecklistItemResponse::from)
                        .toList()
        );
    }
}
