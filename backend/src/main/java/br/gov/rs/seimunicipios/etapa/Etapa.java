package br.gov.rs.seimunicipios.etapa;

import br.gov.rs.seimunicipios.checklist.ChecklistItem;
import br.gov.rs.seimunicipios.municipio.Municipio;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(length = 500)
    private String descricao;

    @Column(nullable = false)
    private Integer ordem = 0;

    @OneToMany(mappedBy = "etapa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChecklistItem> checklistItems = new ArrayList<>();

    public List<ChecklistItem> getChecklistItemsOrdenados() {
        return checklistItems.stream()
                .sorted(Comparator.comparing(ChecklistItem::getOrdem))
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
}
