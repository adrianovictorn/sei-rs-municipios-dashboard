package br.gov.rs.seimunicipios.municipio;

import br.gov.rs.seimunicipios.municipio.dto.MunicipioDetailResponse;
import br.gov.rs.seimunicipios.municipio.dto.MunicipioRequest;
import br.gov.rs.seimunicipios.municipio.dto.MunicipioSummaryResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/municipios")
@RequiredArgsConstructor
public class MunicipioController {

    private final MunicipioService municipioService;

    @GetMapping
    public List<MunicipioSummaryResponse> findAll() {
        return municipioService.findAll();
    }

    @GetMapping("/{id}")
    public MunicipioDetailResponse findById(@PathVariable Long id) {
        return municipioService.findDetailById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MunicipioDetailResponse create(@Valid @RequestBody MunicipioRequest request) {
        return municipioService.create(request);
    }

    @PutMapping("/{id}")
    public MunicipioDetailResponse update(@PathVariable Long id, @Valid @RequestBody MunicipioRequest request) {
        return municipioService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        municipioService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
