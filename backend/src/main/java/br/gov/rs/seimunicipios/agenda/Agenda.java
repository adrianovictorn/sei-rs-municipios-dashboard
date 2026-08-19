package br.gov.rs.seimunicipios.agenda;

import br.gov.rs.seimunicipios.municipio.Municipio;
import br.gov.rs.seimunicipios.tipoagenda.TipoAgenda;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "agenda")
@Getter
@Setter
public class Agenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "municipio_id", nullable = false)
    private Municipio municipio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_agenda_id")
    private TipoAgenda tipoAgenda;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Column(length = 300)
    private String local;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @Column(nullable = false)
    private boolean realizada = false;
}
