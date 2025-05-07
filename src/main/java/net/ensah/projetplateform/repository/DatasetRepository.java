package net.ensah.projetplateform.repository;

import net.ensah.projetplateform.entities.Dataset;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DatasetRepository extends JpaRepository<Dataset, Long> {
}