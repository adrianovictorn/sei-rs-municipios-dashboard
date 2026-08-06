package br.gov.rs.seimunicipios.municipio.dto;

import br.gov.rs.seimunicipios.etapa.Etapa;
import br.gov.rs.seimunicipios.etapa.EtapaStatus;
import br.gov.rs.seimunicipios.municipio.Municipio;

import java.time.LocalDate;
import java.util.List;

public record MunicipioSummaryResponse(
        Long id,
        String nome,
        String regiao,
        LocalDate dataInicio,
        int progresso,
        int totalEtapas,
        long etapasConcluidas,
        String etapaAtual
) {
    public static MunicipioSummaryResponse from(Municipio municipio) {
        List<Etapa> etapas = municipio.getEtapasOrdenadas();

        int progresso = etapas.isEmpty()
                ? 0
                : (int) Math.round(etapas.stream().mapToInt(Etapa::getProgresso).average().orElse(0));

        long concluidas = etapas.stream().filter(e -> e.getStatus() == EtapaStatus.CONCLUIDA).count();

        String etapaAtual = etapas.stream()
                .filter(e -> e.getStatus() != EtapaStatus.CONCLUIDA)
                .findFirst()
                .map(Etapa::getNome)
                .orElse(etapas.isEmpty() ? null : "Concluído");

        return new MunicipioSummaryResponse(
                municipio.getId(),
                municipio.getNome(),
                municipio.getRegiao(),
                municipio.getDataInicio(),
                progresso,
                etapas.size(),
                concluidas,
                etapaAtual
        );
    }
}
