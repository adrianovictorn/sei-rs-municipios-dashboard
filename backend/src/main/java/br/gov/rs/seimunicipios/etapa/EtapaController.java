package br.gov.rs.seimunicipios.etapa;

import br.gov.rs.seimunicipios.etapa.dto.EtapaRequest;
import br.gov.rs.seimunicipios.etapa.dto.EtapaResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class EtapaController {

    private final EtapaService etapaService;

    @PostMapping("/api/municipios/{municipioId}/etapas")
    @ResponseStatus(HttpStatus.CREATED)
    public EtapaResponse addEtapa(@PathVariable Long municipioId, @Valid @RequestBody EtapaRequest request) {
        return etapaService.addEtapa(municipioId, request);
    }

    @PutMapping("/api/etapas/{id}")
    public EtapaResponse updateEtapa(@PathVariable Long id, @Valid @RequestBody EtapaRequest request) {
        return etapaService.updateEtapa(id, request);
    }

    @DeleteMapping("/api/etapas/{id}")
    public ResponseEntity<Void> deleteEtapa(@PathVariable Long id) {
        etapaService.deleteEtapa(id);
        return ResponseEntity.noContent().build();
    }
}
