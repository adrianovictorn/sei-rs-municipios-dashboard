package br.gov.rs.seimunicipios.tipoagenda;

import br.gov.rs.seimunicipios.common.NotFoundException;
import br.gov.rs.seimunicipios.tipoagenda.dto.TipoAgendaRequest;
import br.gov.rs.seimunicipios.tipoagenda.dto.TipoAgendaResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TipoAgendaService {

    private final TipoAgendaRepository tipoAgendaRepository;

    @Transactional(readOnly = true)
    public List<TipoAgendaResponse> findAll() {
        return tipoAgendaRepository.findAll().stream()
                .map(TipoAgendaResponse::from)
                .toList();
    }

    public TipoAgendaResponse create(TipoAgendaRequest request) {
        TipoAgenda tipoAgenda = new TipoAgenda();
        tipoAgenda.setNome(request.nome());
        return TipoAgendaResponse.from(tipoAgendaRepository.save(tipoAgenda));
    }

    public TipoAgendaResponse update(Long id, TipoAgendaRequest request) {
        TipoAgenda tipoAgenda = findEntityById(id);
        tipoAgenda.setNome(request.nome());
        return TipoAgendaResponse.from(tipoAgendaRepository.save(tipoAgenda));
    }

    public void delete(Long id) {
        TipoAgenda tipoAgenda = findEntityById(id);
        tipoAgendaRepository.delete(tipoAgenda);
    }

    private TipoAgenda findEntityById(Long id) {
        return tipoAgendaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tipo de agenda não encontrado: " + id));
    }
}
