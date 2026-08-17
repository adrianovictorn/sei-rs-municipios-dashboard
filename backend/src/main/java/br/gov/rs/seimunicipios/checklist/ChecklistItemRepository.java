package br.gov.rs.seimunicipios.checklist;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChecklistItemRepository extends JpaRepository<ChecklistItem, Long> {
    List<ChecklistItem> findByChecklistItemTemplateId(Long checklistItemTemplateId);
}
