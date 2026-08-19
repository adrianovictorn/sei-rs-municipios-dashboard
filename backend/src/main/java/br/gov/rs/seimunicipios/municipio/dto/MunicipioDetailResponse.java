package br.gov.rs.seimunicipios.municipio.dto;

import br.gov.rs.seimunicipios.etapa.Etapa;
import br.gov.rs.seimunicipios.etapa.dto.EtapaResponse;
import br.gov.rs.seimunicipios.municipio.Municipio;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record MunicipioDetailResponse(
        Long id,
        String nome,
        String codigoIbge,
        String regiao,
        Integer populacao,
        String patrocinadorExecutivo,
        String pontoFocalNome,
        String pontoFocalEmail,
        String pontoFocalTelefone,
        LocalDate dataInicio,
        LocalDate dataPrevistaGolive,
        String observacoes,
        Long equipeId,
        String equipeNome,
        boolean parado,
        int progresso,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<EtapaResponse> etapas
) {
    public static MunicipioDetailResponse from(Municipio municipio) {
        List<Etapa> etapas = municipio.getEtapasOrdenadas();

        int progresso = etapas.isEmpty()
                ? 0
                : (int) Math.round(etapas.stream().mapToInt(Etapa::getProgresso).average().orElse(0));

        return new MunicipioDetailResponse(
                municipio.getId(),
                municipio.getNome(),
                municipio.getCodigoIbge(),
                municipio.getRegiao(),
                municipio.getPopulacao(),
                municipio.getPatrocinadorExecutivo(),
                municipio.getPontoFocalNome(),
                municipio.getPontoFocalEmail(),
                municipio.getPontoFocalTelefone(),
                municipio.getDataInicio(),
                municipio.getDataPrevistaGolive(),
                municipio.getObservacoes(),
                municipio.getEquipe() != null ? municipio.getEquipe().getId() : null,
                municipio.getEquipe() != null ? municipio.getEquipe().getNome() : null,
                municipio.isParado(),
                progresso,
                municipio.getCreatedAt(),
                municipio.getUpdatedAt(),
                etapas.stream().map(EtapaResponse::from).toList()
        );
    }
}
