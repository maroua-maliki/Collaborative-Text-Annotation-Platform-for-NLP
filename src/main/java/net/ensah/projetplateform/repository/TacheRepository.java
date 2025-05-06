package net.ensah.projetplateform.repository;

import net.ensah.projetplateform.entities.Taches;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TacheRepository extends JpaRepository<Taches, Long> {
}
