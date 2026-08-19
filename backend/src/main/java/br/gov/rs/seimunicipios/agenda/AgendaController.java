package br.gov.rs.seimunicipios.agenda;

import br.gov.rs.seimunicipios.agenda.dto.AgendaRequest;
import br.gov.rs.seimunicipios.agenda.dto.AgendaResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AgendaController {

    private final AgendaService agendaService;

    @GetMapping("/api/agendas")
    public List<AgendaResponse> findAll() {
        return agendaService.findAll();
    }

    @PostMapping("/api/municipios/{municipioId}/agendas")
    @ResponseStatus(HttpStatus.CREATED)
    public AgendaResponse create(@PathVariable Long municipioId, @Valid @RequestBody AgendaRequest request) {
        return agendaService.create(municipioId, request);
    }

    @PutMapping("/api/agendas/{id}")
    public AgendaResponse update(@PathVariable Long id, @Valid @RequestBody AgendaRequest request) {
        return agendaService.update(id, request);
    }

    @DeleteMapping("/api/agendas/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        agendaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
