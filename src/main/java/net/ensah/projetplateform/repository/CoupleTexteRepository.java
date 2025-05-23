package net.ensah.projetplateform.repository;

import net.ensah.projetplateform.entities.CoupleTexte;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CoupleTexteRepository extends JpaRepository<CoupleTexte, Long> {
    Page<CoupleTexte> findByDatasetId(Long datasetId, Pageable pageable);
    List<CoupleTexte> findByTachesId(Long tacheId);

    @Query("SELECT COUNT(ct) FROM CoupleTexte ct WHERE ct.taches.annotateur.login = :username AND ct.annotations IS NOT NULL")
    long countByTachesAnnotateurLoginAndAnnotationsIsNotNull(@Param("username") String username);
}
