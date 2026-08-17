package br.gov.rs.seimunicipios.etapa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EtapaRepository extends JpaRepository<Etapa, Long> {
    List<Etapa> findByEtapaTemplateId(Long etapaTemplateId);
}
