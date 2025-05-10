package net.ensah.projetplateform.repository;

import net.ensah.projetplateform.entities.Annotateur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AnnotateurRepository extends JpaRepository<Annotateur, Long> {
    Page<Annotateur> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(String nom, String prenom, Pageable pageable);

    Page<Annotateur> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseAndIsActive(String nom, String prenom, Boolean isActive, Pageable pageable);

    @Query("SELECT a FROM Annotateur a WHERE a.isActive = true AND (LOWER(a.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(a.prenom) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Annotateur> findActiveAnnotateursByKeyword(@Param("keyword") String keyword, Pageable pageable);

    Page<Annotateur> findByIsActiveTrue(Pageable pageable);

    Annotateur findByLogin(String login);

}

