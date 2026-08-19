package br.gov.rs.seimunicipios.tipoagenda;

import br.gov.rs.seimunicipios.tipoagenda.dto.TipoAgendaRequest;
import br.gov.rs.seimunicipios.tipoagenda.dto.TipoAgendaResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-agenda")
@RequiredArgsConstructor
public class TipoAgendaController {

    private final TipoAgendaService tipoAgendaService;

    @GetMapping
    public List<TipoAgendaResponse> findAll() {
        return tipoAgendaService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TipoAgendaResponse create(@Valid @RequestBody TipoAgendaRequest request) {
        return tipoAgendaService.create(request);
    }

    @PutMapping("/{id}")
    public TipoAgendaResponse update(@PathVariable Long id, @Valid @RequestBody TipoAgendaRequest request) {
        return tipoAgendaService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tipoAgendaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
