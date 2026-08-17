package br.gov.rs.seimunicipios.etapa;

import br.gov.rs.seimunicipios.checklist.ChecklistItemTemplate;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Entity
@Table(name = "etapa_template")
@Getter
@Setter
public class EtapaTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(length = 500)
    private String descricao;

    @Column(nullable = false)
    private Integer ordem = 0;

    @Column(name = "duracao_dias")
    private Integer duracaoDias;

    @Column(name = "exibir_matriz", nullable = false)
    private boolean exibirMatriz = false;

    @Column(name = "ordem_matriz")
    private Integer ordemMatriz;

    @OneToMany(mappedBy = "etapaTemplate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChecklistItemTemplate> itens = new ArrayList<>();

    public List<ChecklistItemTemplate> getItensOrdenados() {
        return itens.stream()
                .sorted(Comparator.comparing(ChecklistItemTemplate::getOrdem))
                .toList();
    }
}
