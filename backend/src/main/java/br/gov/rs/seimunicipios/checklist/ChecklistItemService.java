package br.gov.rs.seimunicipios.checklist;

import br.gov.rs.seimunicipios.checklist.dto.ChecklistItemRequest;
import br.gov.rs.seimunicipios.checklist.dto.ChecklistItemResponse;
import br.gov.rs.seimunicipios.checklist.dto.ChecklistItemUpdateRequest;
import br.gov.rs.seimunicipios.common.IllegalOperationException;
import br.gov.rs.seimunicipios.common.NotFoundException;
import br.gov.rs.seimunicipios.etapa.Etapa;
import br.gov.rs.seimunicipios.etapa.EtapaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class ChecklistItemService {

    private final ChecklistItemRepository checklistItemRepository;
    private final EtapaService etapaService;

    public ChecklistItemResponse addItem(Long etapaId, ChecklistItemRequest request) {
        Etapa etapa = etapaService.findEntityById(etapaId);

        ChecklistItem item = new ChecklistItem();
        item.setEtapa(etapa);
        item.setDescricao(request.descricao());
        item.setOrdem(request.ordem() != null ? request.ordem() : etapa.getChecklistItems().size());
        item.setDataInicio(request.dataInicio());
        item.setDataFim(request.dataFim());
        item.setDuracaoDias(request.duracaoDias());
        item.setPercentualPrevisto(request.percentualPrevisto());
        item.setPredecessoras(request.predecessoras());

        etapa.getChecklistItems().add(item);
        return ChecklistItemResponse.from(checklistItemRepository.save(item));
    }

    public ChecklistItemResponse updateItem(Long itemId, ChecklistItemUpdateRequest request) {
        ChecklistItem item = findEntityById(itemId);
        boolean vinculadoAoTemplate = item.getChecklistItemTemplate() != null;

        if (request.descricao() != null) {
            if (vinculadoAoTemplate && !request.descricao().equals(item.getDescricaoEfetiva())) {
                throw new IllegalOperationException(
                        "Esta é uma tarefa padrão; edite a descrição pelo template global de fases.");
            }
            if (!vinculadoAoTemplate) {
                item.setDescricao(request.descricao());
            }
        }

        if (request.ordem() != null) {
            if (vinculadoAoTemplate && !request.ordem().equals(item.getOrdemEfetiva())) {
                throw new IllegalOperationException(
                        "Esta é uma tarefa padrão; edite a ordem pelo template global de fases.");
            }
            if (!vinculadoAoTemplate) {
                item.setOrdem(request.ordem());
            }
        }

        if (request.concluido() != null) {
            item.setConcluido(request.concluido());
            item.setDataConclusao(request.concluido() ? LocalDate.now() : null);
        }

        if (request.dataInicio() != null) {
            item.setDataInicio(request.dataInicio());
        }
        if (request.dataFim() != null) {
            item.setDataFim(request.dataFim());
        }
        if (request.duracaoDias() != null) {
            item.setDuracaoDias(request.duracaoDias());
        }
        if (request.percentualPrevisto() != null) {
            item.setPercentualPrevisto(request.percentualPrevisto());
        }
        if (request.predecessoras() != null) {
            item.setPredecessoras(request.predecessoras());
        }

        return ChecklistItemResponse.from(checklistItemRepository.save(item));
    }

    public void deleteItem(Long itemId) {
        ChecklistItem item = findEntityById(itemId);
        checklistItemRepository.delete(item);
    }

    private ChecklistItem findEntityById(Long id) {
        return checklistItemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item de checklist não encontrado: " + id));
    }
}
