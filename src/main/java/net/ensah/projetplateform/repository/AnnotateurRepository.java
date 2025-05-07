package net.ensah.projetplateform.repository;

import net.ensah.projetplateform.entities.Annotateur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnotateurRepository extends JpaRepository<Annotateur, Long> {
    Page<Annotateur> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(String nom, String prenom , Pageable pageable);
}
