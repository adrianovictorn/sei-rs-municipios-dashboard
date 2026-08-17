package br.gov.rs.seimunicipios.municipio.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record MunicipioRequest(
        @NotBlank String nome,
        String codigoIbge,
        String regiao,
        Integer populacao,
        String patrocinadorExecutivo,
        String pontoFocalNome,
        @Email String pontoFocalEmail,
        String pontoFocalTelefone,
        LocalDate dataInicio,
        String observacoes,
        Long equipeId,
        Boolean parado
) {
}
