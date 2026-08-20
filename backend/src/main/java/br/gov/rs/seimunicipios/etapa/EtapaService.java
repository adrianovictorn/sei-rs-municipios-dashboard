package br.gov.rs.seimunicipios.etapa;

import br.gov.rs.seimunicipios.common.IllegalOperationException;
import br.gov.rs.seimunicipios.common.NotFoundException;
import br.gov.rs.seimunicipios.etapa.dto.EtapaRequest;
import br.gov.rs.seimunicipios.etapa.dto.EtapaResponse;
import br.gov.rs.seimunicipios.municipio.Municipio;
import br.gov.rs.seimunicipios.municipio.MunicipioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class EtapaService {

    private final EtapaRepository etapaRepository;
    private final MunicipioService municipioService;

    public EtapaResponse addEtapa(Long municipioId, EtapaRequest request) {
        Municipio municipio = municipioService.findEntityById(municipioId);

        Etapa etapa = new Etapa();
        etapa.setMunicipio(municipio);
        etapa.setNome(request.nome());
        etapa.setDescricao(request.descricao());
        etapa.setOrdem(request.ordem() != null ? request.ordem() : municipio.getEtapas().size());
        aplicarCronograma(etapa, request);

        municipio.getEtapas().add(etapa);
        return EtapaResponse.from(etapaRepository.save(etapa));
    }

    public EtapaResponse updateEtapa(Long etapaId, EtapaRequest request) {
        Etapa etapa = findEntityById(etapaId);

        boolean tentandoAlterarPadrao = !Objects.equals(request.nome(), etapa.getNomeEfetivo())
                || !Objects.equals(request.descricao(), etapa.getDescricaoEfetiva());

        if (etapa.getEtapaTemplate() != null) {
            if (tentandoAlterarPadrao) {
                throw new IllegalOperationException(
                        "Esta é uma fase padrão; edite nome e descrição pelo template global de fases.");
            }
        } else {
            etapa.setNome(request.nome());
            etapa.setDescricao(request.descricao());
        }

        // A ordem e sempre editavel por municipio, mesmo em fase vinculada a template:
        // uma vez definida aqui, essa fase passa a usar a ordem local (ver Etapa#getOrdemEfetiva)
        // e nao acompanha mais reordenacoes futuras do template.
        if (request.ordem() != null) {
            etapa.setOrdem(request.ordem());
        }

        aplicarCronograma(etapa, request);
        return EtapaResponse.from(etapaRepository.save(etapa));
    }

    public void deleteEtapa(Long etapaId) {
        Etapa etapa = findEntityById(etapaId);
        etapaRepository.delete(etapa);
    }

    public Etapa findEntityById(Long id) {
        return etapaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Etapa não encontrada: " + id));
    }

    private void aplicarCronograma(Etapa etapa, EtapaRequest request) {
        etapa.setDataSolicitacao(request.dataSolicitacao());
        etapa.setDataInicio(request.dataInicio());
        etapa.setDataFim(request.dataFim());
        etapa.setDuracaoDias(request.duracaoDias());
        etapa.setPercentualPrevisto(request.percentualPrevisto());
        etapa.setPredecessoras(request.predecessoras());
    }
}
