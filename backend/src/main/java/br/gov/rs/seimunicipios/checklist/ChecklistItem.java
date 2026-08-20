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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checklist_item_template_id")
    private ChecklistItemTemplate checklistItemTemplate;

    @Column(nullable = false, length = 500)
    private String descricao;

    @Column(nullable = false)
    private boolean concluido = false;

    @Column(name = "data_conclusao")
    private LocalDate dataConclusao;

    /** Ordem local; nula enquanto a tarefa segue a ordem do template ao vivo (ver getOrdemEfetiva). */
    @Column
    private Integer ordem;

    @Column(name = "data_inicio")
    private LocalDate dataInicio;

    @Column(name = "data_fim")
    private LocalDate dataFim;

    @Column(name = "duracao_dias")
    private Integer duracaoDias;

    @Column(name = "percentual_previsto")
    private Integer percentualPrevisto;

    @Column(length = 100)
    private String predecessoras;

    /** Descrição exibida: vem do template global enquanto o item estiver vinculado a um. */
    public String getDescricaoEfetiva() {
        return checklistItemTemplate != null ? checklistItemTemplate.getDescricao() : descricao;
    }

    public Integer getOrdemEfetiva() {
        if (ordem != null) {
            return ordem;
        }
        return checklistItemTemplate != null ? checklistItemTemplate.getOrdem() : 0;
    }

    public Integer getDuracaoEfetiva() {
        return duracaoDias != null ? duracaoDias : (checklistItemTemplate != null ? checklistItemTemplate.getDuracaoDias() : null);
    }

    public Long getTemplateId() {
        return checklistItemTemplate != null ? checklistItemTemplate.getId() : null;
    }
}
