package br.gov.rs.seimunicipios.etapa;

import br.gov.rs.seimunicipios.checklist.dto.ChecklistItemTemplateRequest;
import br.gov.rs.seimunicipios.checklist.dto.ChecklistItemTemplateResponse;
import br.gov.rs.seimunicipios.etapa.dto.EtapaTemplateRequest;
import br.gov.rs.seimunicipios.etapa.dto.EtapaTemplateResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class EtapaTemplateController {

    private final EtapaTemplateService etapaTemplateService;

    @GetMapping("/api/etapa-templates")
    public List<EtapaTemplateResponse> findAll() {
        return etapaTemplateService.findAll();
    }

    @PostMapping("/api/etapa-templates")
    @ResponseStatus(HttpStatus.CREATED)
    public EtapaTemplateResponse create(@Valid @RequestBody EtapaTemplateRequest request) {
        return etapaTemplateService.createFase(request);
    }

    @PutMapping("/api/etapa-templates/{id}")
    public EtapaTemplateResponse update(@PathVariable Long id, @Valid @RequestBody EtapaTemplateRequest request) {
        return etapaTemplateService.updateFase(id, request);
    }

    @DeleteMapping("/api/etapa-templates/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @RequestParam(name = "emTodosMunicipios", defaultValue = "false") boolean emTodosMunicipios
    ) {
        if (emTodosMunicipios) {
            etapaTemplateService.deleteFaseEmTodosMunicipios(id);
        } else {
            etapaTemplateService.deleteFase(id);
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/api/etapa-templates/{faseId}/itens")
    @ResponseStatus(HttpStatus.CREATED)
    public ChecklistItemTemplateResponse addItem(@PathVariable Long faseId, @Valid @RequestBody ChecklistItemTemplateRequest request) {
        return etapaTemplateService.addTarefa(faseId, request);
    }

    @PutMapping("/api/checklist-item-templates/{id}")
    public ChecklistItemTemplateResponse updateItem(@PathVariable Long id, @Valid @RequestBody ChecklistItemTemplateRequest request) {
        return etapaTemplateService.updateTarefa(id, request);
    }

    @DeleteMapping("/api/checklist-item-templates/{id}")
    public ResponseEntity<Void> deleteItem(
            @PathVariable Long id,
            @RequestParam(name = "emTodosMunicipios", defaultValue = "false") boolean emTodosMunicipios
    ) {
        if (emTodosMunicipios) {
            etapaTemplateService.deleteTarefaEmTodosMunicipios(id);
        } else {
            etapaTemplateService.deleteTarefa(id);
        }
        return ResponseEntity.noContent().build();
    }
}
