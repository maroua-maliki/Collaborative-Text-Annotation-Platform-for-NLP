package net.ensah.projetplateform.repository;

import net.ensah.projetplateform.entities.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Utilisateur findByLogin(String login);
}
