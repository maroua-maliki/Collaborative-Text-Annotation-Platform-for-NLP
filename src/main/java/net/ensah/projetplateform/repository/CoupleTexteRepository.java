package net.ensah.projetplateform.repository;

import net.ensah.projetplateform.entities.CoupleTexte;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CoupleTexteRepository extends JpaRepository<CoupleTexte, Long> {
    Page<CoupleTexte> findByDatasetId(Long datasetId, Pageable pageable);
}
