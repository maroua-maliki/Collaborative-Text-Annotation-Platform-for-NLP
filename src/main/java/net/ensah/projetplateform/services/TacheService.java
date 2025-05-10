package net.ensah.projetplateform.services;

import net.ensah.projetplateform.entities.Taches;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TacheService {
    Page<Taches> findAllTaches(Pageable pageable);
    Page<Taches> findTachesByAnnotateur(String username, Pageable pageable);
    Page<Taches> findTachesByAnnotateurAndKeyword(String username, String keyword, Pageable pageable);
}