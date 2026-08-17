package br.gov.rs.seimunicipios.municipio;

import br.gov.rs.seimunicipios.equipe.Equipe;
import br.gov.rs.seimunicipios.etapa.Etapa;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Entity
@Table(name = "municipio")
@Getter
@Setter
public class Municipio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(name = "codigo_ibge", length = 10)
    private String codigoIbge;

    @Column(length = 100)
    private String regiao;

    private Integer populacao;

    @Column(name = "patrocinador_executivo", length = 150)
    private String patrocinadorExecutivo;

    @Column(name = "ponto_focal_nome", length = 150)
    private String pontoFocalNome;

    @Column(name = "ponto_focal_email", length = 150)
    private String pontoFocalEmail;

    @Column(name = "ponto_focal_telefone", length = 30)
    private String pontoFocalTelefone;

    @Column(name = "data_inicio")
    private LocalDate dataInicio;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipe_id")
    private Equipe equipe;

    @Column(nullable = false)
    private boolean parado = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "municipio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Etapa> etapas = new ArrayList<>();

    @PrePersist
    void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public List<Etapa> getEtapasOrdenadas() {
        return etapas.stream()
                .sorted(Comparator.comparing(Etapa::getOrdemEfetiva))
                .toList();
    }
}
