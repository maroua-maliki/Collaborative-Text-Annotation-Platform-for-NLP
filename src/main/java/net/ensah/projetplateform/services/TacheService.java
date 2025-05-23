package net.ensah.projetplateform.services;

import net.ensah.projetplateform.entities.CoupleTexte;
import net.ensah.projetplateform.entities.Taches;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface TacheService {
    Page<Taches> findAllTaches(Pageable pageable);
    Page<Taches> findTachesByAnnotateur(String username, Pageable pageable);
    Page<Taches> findTachesByAnnotateurAndKeyword(String username, String keyword, Pageable pageable);
    Taches getTacheById(Long id);
    List<CoupleTexte> getCoupleTextesByTacheId(Long tacheId);
    // Ajouter cette méthode à l'interface
    List<Map<String, Object>> getTachesAvecAvancement(String username, Pageable pageable);
    List<Map<String, Object>> getTachesAvecAvancementByKeyword(String username, String keyword, Pageable pageable);
    List<Taches> getTachesActive();
    void updateOverdueTasks();

}