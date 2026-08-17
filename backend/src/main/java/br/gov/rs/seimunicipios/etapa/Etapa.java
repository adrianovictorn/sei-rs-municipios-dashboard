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

    @Column(nullable = false)
    private Integer ordem = 0;

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

    public Integer getOrdemEfetiva() {
        return etapaTemplate != null ? etapaTemplate.getOrdem() : ordem;
    }

    public Integer getDuracaoEfetiva() {
        return duracaoDias != null ? duracaoDias : (etapaTemplate != null ? etapaTemplate.getDuracaoDias() : null);
    }

    public Long getTemplateId() {
        return etapaTemplate != null ? etapaTemplate.getId() : null;
    }
}
