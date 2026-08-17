package br.gov.rs.seimunicipios.equipe.dto;

import br.gov.rs.seimunicipios.equipe.Equipe;

public record EquipeResponse(
        Long id,
        String nome
) {
    public static EquipeResponse from(Equipe equipe) {
        return new EquipeResponse(equipe.getId(), equipe.getNome());
    }
}
