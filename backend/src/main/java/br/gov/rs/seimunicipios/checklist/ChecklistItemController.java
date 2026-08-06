package br.gov.rs.seimunicipios.checklist;

import br.gov.rs.seimunicipios.checklist.dto.ChecklistItemRequest;
import br.gov.rs.seimunicipios.checklist.dto.ChecklistItemResponse;
import br.gov.rs.seimunicipios.checklist.dto.ChecklistItemUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ChecklistItemController {

    private final ChecklistItemService checklistItemService;

    @PostMapping("/api/etapas/{etapaId}/checklist-items")
    @ResponseStatus(HttpStatus.CREATED)
    public ChecklistItemResponse addItem(@PathVariable Long etapaId, @Valid @RequestBody ChecklistItemRequest request) {
        return checklistItemService.addItem(etapaId, request);
    }

    @PutMapping("/api/checklist-items/{id}")
    public ChecklistItemResponse updateItem(@PathVariable Long id, @RequestBody ChecklistItemUpdateRequest request) {
        return checklistItemService.updateItem(id, request);
    }

    @DeleteMapping("/api/checklist-items/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        checklistItemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }
}
