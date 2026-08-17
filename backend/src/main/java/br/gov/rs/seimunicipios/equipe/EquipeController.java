package br.gov.rs.seimunicipios.equipe;

import br.gov.rs.seimunicipios.equipe.dto.EquipeRequest;
import br.gov.rs.seimunicipios.equipe.dto.EquipeResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipes")
@RequiredArgsConstructor
public class EquipeController {

    private final EquipeService equipeService;

    @GetMapping
    public List<EquipeResponse> findAll() {
        return equipeService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EquipeResponse create(@Valid @RequestBody EquipeRequest request) {
        return equipeService.create(request);
    }

    @PutMapping("/{id}")
    public EquipeResponse update(@PathVariable Long id, @Valid @RequestBody EquipeRequest request) {
        return equipeService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        equipeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
