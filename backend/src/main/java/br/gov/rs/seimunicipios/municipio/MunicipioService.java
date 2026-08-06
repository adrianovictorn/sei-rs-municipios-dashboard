package br.gov.rs.seimunicipios.municipio;

import br.gov.rs.seimunicipios.checklist.ChecklistItem;
import br.gov.rs.seimunicipios.common.NotFoundException;
import br.gov.rs.seimunicipios.etapa.Etapa;
import br.gov.rs.seimunicipios.etapa.EtapaSeed;
import br.gov.rs.seimunicipios.municipio.dto.MunicipioDetailResponse;
import br.gov.rs.seimunicipios.municipio.dto.MunicipioRequest;
import br.gov.rs.seimunicipios.municipio.dto.MunicipioSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MunicipioService {

    private final MunicipioRepository municipioRepository;

    @Transactional(readOnly = true)
    public List<MunicipioSummaryResponse> findAll() {
        return municipioRepository.findAll().stream()
                .map(MunicipioSummaryResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public MunicipioDetailResponse findDetailById(Long id) {
        return MunicipioDetailResponse.from(findEntityById(id));
    }

    public Municipio findEntityById(Long id) {
        return municipioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Município não encontrado: " + id));
    }

    public MunicipioDetailResponse create(MunicipioRequest request) {
        Municipio municipio = new Municipio();
        applyRequest(municipio, request);
        seedEtapas(municipio);
        return MunicipioDetailResponse.from(municipioRepository.save(municipio));
    }

    public MunicipioDetailResponse update(Long id, MunicipioRequest request) {
        Municipio municipio = findEntityById(id);
        applyRequest(municipio, request);
        return MunicipioDetailResponse.from(municipioRepository.save(municipio));
    }

    public void delete(Long id) {
        Municipio municipio = findEntityById(id);
        municipioRepository.delete(municipio);
    }

    private void applyRequest(Municipio municipio, MunicipioRequest request) {
        municipio.setNome(request.nome());
        municipio.setCodigoIbge(request.codigoIbge());
        municipio.setRegiao(request.regiao());
        municipio.setPopulacao(request.populacao());
        municipio.setPatrocinadorExecutivo(request.patrocinadorExecutivo());
        municipio.setPontoFocalNome(request.pontoFocalNome());
        municipio.setPontoFocalEmail(request.pontoFocalEmail());
        municipio.setPontoFocalTelefone(request.pontoFocalTelefone());
        municipio.setDataInicio(request.dataInicio());
        municipio.setObservacoes(request.observacoes());
    }

    private void seedEtapas(Municipio municipio) {
        int etapaOrdem = 0;
        for (EtapaSeed.EtapaTemplate etapaTemplate : EtapaSeed.padrao()) {
            Etapa etapa = new Etapa();
            etapa.setMunicipio(municipio);
            etapa.setNome(etapaTemplate.nome());
            etapa.setDescricao(etapaTemplate.descricao());
            etapa.setOrdem(etapaOrdem++);

            int itemOrdem = 0;
            for (EtapaSeed.ItemTemplate itemTemplate : etapaTemplate.itens()) {
                ChecklistItem item = new ChecklistItem();
                item.setEtapa(etapa);
                item.setDescricao(itemTemplate.descricao());
                item.setOrdem(itemOrdem++);
                etapa.getChecklistItems().add(item);
            }

            municipio.getEtapas().add(etapa);
        }
    }
}
