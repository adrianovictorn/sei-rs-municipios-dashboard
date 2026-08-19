package br.gov.rs.seimunicipios.municipio;

import br.gov.rs.seimunicipios.checklist.ChecklistItem;
import br.gov.rs.seimunicipios.checklist.ChecklistItemTemplate;
import br.gov.rs.seimunicipios.common.NotFoundException;
import br.gov.rs.seimunicipios.equipe.EquipeRepository;
import br.gov.rs.seimunicipios.etapa.Etapa;
import br.gov.rs.seimunicipios.etapa.EtapaTemplate;
import br.gov.rs.seimunicipios.etapa.EtapaTemplateRepository;
import br.gov.rs.seimunicipios.municipio.dto.MunicipioDetailResponse;
import br.gov.rs.seimunicipios.municipio.dto.MunicipioRequest;
import br.gov.rs.seimunicipios.municipio.dto.MunicipioSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MunicipioService {

    private final MunicipioRepository municipioRepository;
    private final EtapaTemplateRepository etapaTemplateRepository;
    private final EquipeRepository equipeRepository;

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
        municipio.setDataPrevistaGolive(request.dataPrevistaGolive());
        municipio.setObservacoes(request.observacoes());
        municipio.setParado(Boolean.TRUE.equals(request.parado()));
        municipio.setEquipe(request.equipeId() != null
                ? equipeRepository.findById(request.equipeId())
                        .orElseThrow(() -> new NotFoundException("Equipe não encontrada: " + request.equipeId()))
                : null);
    }

    /**
     * Cria as fases/tarefas do municipio a partir do template global (etapa_template/
     * checklist_item_template), ja vinculadas a ele. Enquanto o vinculo existir, editar o
     * template propaga o nome/descricao para este municipio automaticamente.
     */
    private void seedEtapas(Municipio municipio) {
        List<EtapaTemplate> etapaTemplates = etapaTemplateRepository.findAll().stream()
                .sorted(Comparator.comparing(EtapaTemplate::getOrdem))
                .toList();

        for (EtapaTemplate etapaTemplate : etapaTemplates) {
            Etapa etapa = new Etapa();
            etapa.setMunicipio(municipio);
            etapa.setEtapaTemplate(etapaTemplate);
            etapa.setNome(etapaTemplate.getNome());
            etapa.setDescricao(etapaTemplate.getDescricao());
            etapa.setOrdem(etapaTemplate.getOrdem());

            for (ChecklistItemTemplate itemTemplate : etapaTemplate.getItensOrdenados()) {
                ChecklistItem item = new ChecklistItem();
                item.setEtapa(etapa);
                item.setChecklistItemTemplate(itemTemplate);
                item.setDescricao(itemTemplate.getDescricao());
                item.setOrdem(itemTemplate.getOrdem());
                etapa.getChecklistItems().add(item);
            }

            municipio.getEtapas().add(etapa);
        }
    }
}
