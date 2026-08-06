package br.gov.rs.seimunicipios.etapa.dto;

import br.gov.rs.seimunicipios.checklist.dto.ChecklistItemResponse;
import br.gov.rs.seimunicipios.etapa.Etapa;
import br.gov.rs.seimunicipios.etapa.EtapaStatus;

import java.util.List;

public record EtapaResponse(
        Long id,
        String nome,
        String descricao,
        Integer ordem,
        int progresso,
        EtapaStatus status,
        List<ChecklistItemResponse> checklistItems
) {
    public static EtapaResponse from(Etapa etapa) {
        return new EtapaResponse(
                etapa.getId(),
                etapa.getNome(),
                etapa.getDescricao(),
                etapa.getOrdem(),
                etapa.getProgresso(),
                etapa.getStatus(),
                etapa.getChecklistItemsOrdenados().stream()
                        .map(ChecklistItemResponse::from)
                        .toList()
        );
    }
}
