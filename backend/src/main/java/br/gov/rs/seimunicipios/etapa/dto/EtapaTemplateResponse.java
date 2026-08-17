package br.gov.rs.seimunicipios.etapa.dto;

import br.gov.rs.seimunicipios.checklist.dto.ChecklistItemTemplateResponse;
import br.gov.rs.seimunicipios.etapa.EtapaTemplate;

import java.util.List;

public record EtapaTemplateResponse(
        Long id,
        String nome,
        String descricao,
        Integer ordem,
        Integer duracaoDias,
        List<ChecklistItemTemplateResponse> itens
) {
    public static EtapaTemplateResponse from(EtapaTemplate template) {
        return new EtapaTemplateResponse(
                template.getId(),
                template.getNome(),
                template.getDescricao(),
                template.getOrdem(),
                template.getDuracaoDias(),
                template.getItensOrdenados().stream()
                        .map(ChecklistItemTemplateResponse::from)
                        .toList()
        );
    }
}
