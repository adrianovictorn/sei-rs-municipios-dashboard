package br.gov.rs.seimunicipios.equipe;

import br.gov.rs.seimunicipios.common.NotFoundException;
import br.gov.rs.seimunicipios.equipe.dto.EquipeRequest;
import br.gov.rs.seimunicipios.equipe.dto.EquipeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EquipeService {

    private final EquipeRepository equipeRepository;

    @Transactional(readOnly = true)
    public List<EquipeResponse> findAll() {
        return equipeRepository.findAll().stream()
                .map(EquipeResponse::from)
                .toList();
    }

    public EquipeResponse create(EquipeRequest request) {
        Equipe equipe = new Equipe();
        equipe.setNome(request.nome());
        return EquipeResponse.from(equipeRepository.save(equipe));
    }

    public EquipeResponse update(Long id, EquipeRequest request) {
        Equipe equipe = findEntityById(id);
        equipe.setNome(request.nome());
        return EquipeResponse.from(equipeRepository.save(equipe));
    }

    public void delete(Long id) {
        Equipe equipe = findEntityById(id);
        equipeRepository.delete(equipe);
    }

    private Equipe findEntityById(Long id) {
        return equipeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Equipe não encontrada: " + id));
    }
}
