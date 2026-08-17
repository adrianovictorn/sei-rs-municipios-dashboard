package br.gov.rs.seimunicipios.etapa;

import br.gov.rs.seimunicipios.checklist.ChecklistItem;
import br.gov.rs.seimunicipios.checklist.ChecklistItemRepository;
import br.gov.rs.seimunicipios.checklist.ChecklistItemTemplate;
import br.gov.rs.seimunicipios.checklist.ChecklistItemTemplateRepository;
import br.gov.rs.seimunicipios.checklist.dto.ChecklistItemTemplateRequest;
import br.gov.rs.seimunicipios.checklist.dto.ChecklistItemTemplateResponse;
import br.gov.rs.seimunicipios.common.NotFoundException;
import br.gov.rs.seimunicipios.etapa.dto.EtapaTemplateRequest;
import br.gov.rs.seimunicipios.etapa.dto.EtapaTemplateResponse;
import br.gov.rs.seimunicipios.municipio.Municipio;
import br.gov.rs.seimunicipios.municipio.MunicipioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

/**
 * Fases e tarefas padrao, compartilhadas por todos os municipios. Ajustar nome, descricao
 * ou ordem aqui reflete automaticamente em todo municipio vinculado, pois Etapa/ChecklistItem
 * leem esses campos ao vivo do template (ver Etapa#getNomeEfetivo etc.) enquanto o vinculo
 * existir.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class EtapaTemplateService {

    private final EtapaTemplateRepository etapaTemplateRepository;
    private final ChecklistItemTemplateRepository checklistItemTemplateRepository;
    private final MunicipioRepository municipioRepository;
    private final EtapaRepository etapaRepository;
    private final ChecklistItemRepository checklistItemRepository;

    @Transactional(readOnly = true)
    public List<EtapaTemplateResponse> findAll() {
        return etapaTemplateRepository.findAll().stream()
                .sorted(Comparator.comparing(EtapaTemplate::getOrdem))
                .map(EtapaTemplateResponse::from)
                .toList();
    }

    public EtapaTemplateResponse createFase(EtapaTemplateRequest request) {
        EtapaTemplate template = new EtapaTemplate();
        template.setNome(request.nome());
        template.setDescricao(request.descricao());
        template.setOrdem(request.ordem() != null ? request.ordem() : etapaTemplateRepository.findAll().size());
        template.setDuracaoDias(request.duracaoDias());
        etapaTemplateRepository.save(template);

        for (Municipio municipio : municipioRepository.findAll()) {
            Etapa etapa = new Etapa();
            etapa.setMunicipio(municipio);
            etapa.setEtapaTemplate(template);
            etapa.setNome(template.getNome());
            etapa.setDescricao(template.getDescricao());
            etapa.setOrdem(template.getOrdem());
            etapaRepository.save(etapa);
        }

        return EtapaTemplateResponse.from(template);
    }

    public EtapaTemplateResponse updateFase(Long id, EtapaTemplateRequest request) {
        EtapaTemplate template = findEntityById(id);
        template.setNome(request.nome());
        template.setDescricao(request.descricao());
        if (request.ordem() != null) {
            template.setOrdem(request.ordem());
        }
        template.setDuracaoDias(request.duracaoDias());
        return EtapaTemplateResponse.from(etapaTemplateRepository.save(template));
    }

    public void deleteFase(Long id) {
        EtapaTemplate template = findEntityById(id);
        for (Etapa etapa : etapaRepository.findByEtapaTemplateId(id)) {
            desvincularEtapa(etapa, template);
        }
        etapaTemplateRepository.delete(template);
    }

    public ChecklistItemTemplateResponse addTarefa(Long faseTemplateId, ChecklistItemTemplateRequest request) {
        EtapaTemplate faseTemplate = findEntityById(faseTemplateId);

        ChecklistItemTemplate itemTemplate = new ChecklistItemTemplate();
        itemTemplate.setEtapaTemplate(faseTemplate);
        itemTemplate.setDescricao(request.descricao());
        itemTemplate.setOrdem(request.ordem() != null ? request.ordem() : faseTemplate.getItens().size());
        itemTemplate.setDuracaoDias(request.duracaoDias());
        faseTemplate.getItens().add(itemTemplate);
        checklistItemTemplateRepository.save(itemTemplate);

        for (Etapa etapa : etapaRepository.findByEtapaTemplateId(faseTemplateId)) {
            ChecklistItem item = new ChecklistItem();
            item.setEtapa(etapa);
            item.setChecklistItemTemplate(itemTemplate);
            item.setDescricao(itemTemplate.getDescricao());
            item.setOrdem(itemTemplate.getOrdem());
            checklistItemRepository.save(item);
        }

        return ChecklistItemTemplateResponse.from(itemTemplate);
    }

    public ChecklistItemTemplateResponse updateTarefa(Long id, ChecklistItemTemplateRequest request) {
        ChecklistItemTemplate itemTemplate = findItemEntityById(id);
        itemTemplate.setDescricao(request.descricao());
        if (request.ordem() != null) {
            itemTemplate.setOrdem(request.ordem());
        }
        itemTemplate.setDuracaoDias(request.duracaoDias());
        return ChecklistItemTemplateResponse.from(checklistItemTemplateRepository.save(itemTemplate));
    }

    public void deleteTarefa(Long id) {
        ChecklistItemTemplate itemTemplate = findItemEntityById(id);
        for (ChecklistItem item : checklistItemRepository.findByChecklistItemTemplateId(id)) {
            desvincularItem(item, itemTemplate);
        }
        checklistItemTemplateRepository.delete(itemTemplate);
    }

    public EtapaTemplate findEntityById(Long id) {
        return etapaTemplateRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Fase padrão não encontrada: " + id));
    }

    private ChecklistItemTemplate findItemEntityById(Long id) {
        return checklistItemTemplateRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tarefa padrão não encontrada: " + id));
    }

    private void desvincularEtapa(Etapa etapa, EtapaTemplate template) {
        etapa.setNome(template.getNome());
        etapa.setDescricao(template.getDescricao());
        etapa.setOrdem(template.getOrdem());
        etapa.setEtapaTemplate(null);
        for (ChecklistItem item : etapa.getChecklistItems()) {
            if (item.getChecklistItemTemplate() != null && item.getChecklistItemTemplate().getEtapaTemplate().equals(template)) {
                desvincularItem(item, item.getChecklistItemTemplate());
            }
        }
    }

    private void desvincularItem(ChecklistItem item, ChecklistItemTemplate itemTemplate) {
        item.setDescricao(itemTemplate.getDescricao());
        item.setOrdem(itemTemplate.getOrdem());
        item.setChecklistItemTemplate(null);
    }
}
