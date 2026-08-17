package br.gov.rs.seimunicipios.checklist;

import br.gov.rs.seimunicipios.etapa.EtapaTemplate;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "checklist_item_template")
@Getter
@Setter
public class ChecklistItemTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etapa_template_id", nullable = false)
    private EtapaTemplate etapaTemplate;

    @Column(nullable = false, length = 500)
    private String descricao;

    @Column(nullable = false)
    private Integer ordem = 0;

    @Column(name = "duracao_dias")
    private Integer duracaoDias;
}
