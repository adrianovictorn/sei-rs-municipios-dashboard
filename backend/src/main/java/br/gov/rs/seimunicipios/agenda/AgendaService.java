package br.gov.rs.seimunicipios.agenda;

import br.gov.rs.seimunicipios.agenda.dto.AgendaRequest;
import br.gov.rs.seimunicipios.agenda.dto.AgendaResponse;
import br.gov.rs.seimunicipios.common.NotFoundException;
import br.gov.rs.seimunicipios.municipio.Municipio;
import br.gov.rs.seimunicipios.municipio.MunicipioService;
import br.gov.rs.seimunicipios.tipoagenda.TipoAgenda;
import br.gov.rs.seimunicipios.tipoagenda.TipoAgendaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AgendaService {

    private final AgendaRepository agendaRepository;
    private final TipoAgendaRepository tipoAgendaRepository;
    private final MunicipioService municipioService;

    @Transactional(readOnly = true)
    public List<AgendaResponse> findAll() {
        return agendaRepository.findAll().stream()
                .sorted(Comparator.comparing(Agenda::getDataHora))
                .map(AgendaResponse::from)
                .toList();
    }

    public AgendaResponse create(Long municipioId, AgendaRequest request) {
        Municipio municipio = municipioService.findEntityById(municipioId);

        Agenda agenda = new Agenda();
        agenda.setMunicipio(municipio);
        aplicarRequest(agenda, request);

        return AgendaResponse.from(agendaRepository.save(agenda));
    }

    public AgendaResponse update(Long id, AgendaRequest request) {
        Agenda agenda = findEntityById(id);
        aplicarRequest(agenda, request);
        return AgendaResponse.from(agendaRepository.save(agenda));
    }

    public void delete(Long id) {
        Agenda agenda = findEntityById(id);
        agendaRepository.delete(agenda);
    }

    private void aplicarRequest(Agenda agenda, AgendaRequest request) {
        agenda.setTitulo(request.titulo());
        agenda.setDataHora(request.dataHora());
        agenda.setLocal(request.local());
        agenda.setObservacoes(request.observacoes());
        agenda.setRealizada(Boolean.TRUE.equals(request.realizada()));
        agenda.setTipoAgenda(request.tipoAgendaId() != null ? findTipoById(request.tipoAgendaId()) : null);
    }

    private TipoAgenda findTipoById(Long id) {
        return tipoAgendaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tipo de agenda não encontrado: " + id));
    }

    private Agenda findEntityById(Long id) {
        return agendaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Agenda não encontrada: " + id));
    }
}
