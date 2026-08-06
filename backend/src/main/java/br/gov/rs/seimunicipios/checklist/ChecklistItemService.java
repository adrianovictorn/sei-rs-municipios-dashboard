package br.gov.rs.seimunicipios.checklist;

import br.gov.rs.seimunicipios.checklist.dto.ChecklistItemRequest;
import br.gov.rs.seimunicipios.checklist.dto.ChecklistItemResponse;
import br.gov.rs.seimunicipios.checklist.dto.ChecklistItemUpdateRequest;
import br.gov.rs.seimunicipios.common.NotFoundException;
import br.gov.rs.seimunicipios.etapa.Etapa;
import br.gov.rs.seimunicipios.etapa.EtapaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

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

        etapa.getChecklistItems().add(item);
        return ChecklistItemResponse.from(checklistItemRepository.save(item));
    }

    public ChecklistItemResponse updateItem(Long itemId, ChecklistItemUpdateRequest request) {
        ChecklistItem item = findEntityById(itemId);

        if (request.descricao() != null) {
            item.setDescricao(request.descricao());
        }
        if (request.ordem() != null) {
            item.setOrdem(request.ordem());
        }
        if (request.concluido() != null) {
            item.setConcluido(request.concluido());
            item.setDataConclusao(request.concluido() ? LocalDate.now() : null);
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
