package br.gov.rs.seimunicipios.etapa;

import br.gov.rs.seimunicipios.checklist.ChecklistItem;
import br.gov.rs.seimunicipios.municipio.Municipio;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Entity
@Table(name = "etapa")
@Getter
@Setter
public class Etapa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "municipio_id", nullable = false)
    private Municipio municipio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etapa_template_id")
    private EtapaTemplate etapaTemplate;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(length = 500)
    private String descricao;

    /**
     * Ordem local, dona da posicao no roadmap desse municipio. Nula enquanto a fase
     * segue a ordem do template ao vivo (ver getOrdemEfetiva) - so ganha um valor
     * quando o municipio reordena essa fase especificamente, o que passa a valer por
     * cima de qualquer reordenacao futura do template pra essa fase.
     */
    @Column
    private Integer ordem;

    @Column(name = "data_solicitacao")
    private LocalDate dataSolicitacao;

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

    @OneToMany(mappedBy = "etapa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChecklistItem> checklistItems = new ArrayList<>();

    public List<ChecklistItem> getChecklistItemsOrdenados() {
        return checklistItems.stream()
                .sorted(Comparator.comparing(ChecklistItem::getOrdemEfetiva))
                .toList();
    }

    public int getTotalItens() {
        return checklistItems.size();
    }

    public long getItensConcluidos() {
        return checklistItems.stream().filter(ChecklistItem::isConcluido).count();
    }

    public int getProgresso() {
        int total = getTotalItens();
        if (total == 0) {
            return 0;
        }
        return (int) Math.round((getItensConcluidos() * 100.0) / total);
    }

    public EtapaStatus getStatus() {
        if (getTotalItens() == 0) {
            return EtapaStatus.NAO_INICIADA;
        }
        int progresso = getProgresso();
        if (progresso == 0) {
            return EtapaStatus.NAO_INICIADA;
        }
        if (progresso == 100) {
            return EtapaStatus.CONCLUIDA;
        }
        return EtapaStatus.EM_ANDAMENTO;
    }

    /** Nome exibido: vem do template global enquanto a fase estiver vinculada a um. */
    public String getNomeEfetivo() {
        return etapaTemplate != null ? etapaTemplate.getNome() : nome;
    }

    public String getDescricaoEfetiva() {
        return etapaTemplate != null ? etapaTemplate.getDescricao() : descricao;
    }

    /** Ordem exibida: local quando o municipio ja reordenou essa fase, senao a do template ao vivo. */
    public Integer getOrdemEfetiva() {
        if (ordem != null) {
            return ordem;
        }
        return etapaTemplate != null ? etapaTemplate.getOrdem() : 0;
    }

    public Integer getDuracaoEfetiva() {
        return duracaoDias != null ? duracaoDias : (etapaTemplate != null ? etapaTemplate.getDuracaoDias() : null);
    }

    public Long getTemplateId() {
        return etapaTemplate != null ? etapaTemplate.getId() : null;
    }
}
