package br.gov.rs.seimunicipios.checklist;

import br.gov.rs.seimunicipios.etapa.Etapa;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "checklist_item")
@Getter
@Setter
public class ChecklistItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etapa_id", nullable = false)
    private Etapa etapa;

    @Column(nullable = false, length = 500)
    private String descricao;

    @Column(nullable = false)
    private boolean concluido = false;

    @Column(name = "data_conclusao")
    private LocalDate dataConclusao;

    @Column(nullable = false)
    private Integer ordem = 0;
}
