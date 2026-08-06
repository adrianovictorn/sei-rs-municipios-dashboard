package br.gov.rs.seimunicipios.etapa;

import br.gov.rs.seimunicipios.common.NotFoundException;
import br.gov.rs.seimunicipios.etapa.dto.EtapaRequest;
import br.gov.rs.seimunicipios.etapa.dto.EtapaResponse;
import br.gov.rs.seimunicipios.municipio.Municipio;
import br.gov.rs.seimunicipios.municipio.MunicipioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        municipio.getEtapas().add(etapa);
        return EtapaResponse.from(etapaRepository.save(etapa));
    }

    public EtapaResponse updateEtapa(Long etapaId, EtapaRequest request) {
        Etapa etapa = findEntityById(etapaId);
        etapa.setNome(request.nome());
        etapa.setDescricao(request.descricao());
        if (request.ordem() != null) {
            etapa.setOrdem(request.ordem());
        }
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
}
