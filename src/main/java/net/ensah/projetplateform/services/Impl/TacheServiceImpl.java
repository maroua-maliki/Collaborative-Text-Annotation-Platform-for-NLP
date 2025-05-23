package net.ensah.projetplateform.services.Impl;

import net.ensah.projetplateform.entities.CoupleTexte;
import net.ensah.projetplateform.entities.Taches;
import net.ensah.projetplateform.repository.CoupleTexteRepository;
import net.ensah.projetplateform.repository.TacheRepository;
import net.ensah.projetplateform.services.TacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TacheServiceImpl implements TacheService {

    @Autowired
    private TacheRepository tacheRepository;

    @Autowired
    private CoupleTexteRepository coupleTexteRepository;


    @Override
    public Page<Taches> findAllTaches(Pageable pageable) {
        return tacheRepository.findAll(pageable);
    }
    @Override
    public Page<Taches> findTachesByAnnotateur(String username, Pageable pageable) {
        return tacheRepository.findByAnnotateurLogin(username, pageable);
    }

    @Override
    public Page<Taches> findTachesByAnnotateurAndKeyword(String username, String keyword, Pageable pageable) {
        return tacheRepository.findByAnnotateurLoginAndKeyword(username, keyword, pageable);
    }

    @Override
    public Taches getTacheById(Long id) {
        return tacheRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée avec l'ID: " + id));
    }

    @Override
    public List<CoupleTexte> getCoupleTextesByTacheId(Long tacheId) {
        return coupleTexteRepository.findByTachesId(tacheId);
    }

    @Override
    public List<Map<String, Object>> getTachesAvecAvancement(String username, Pageable pageable) {
        Page<Taches> pageTaches = findTachesByAnnotateur(username, pageable);
        return calculerAvancementTaches(pageTaches.getContent());
    }

    @Override
    public List<Map<String, Object>> getTachesAvecAvancementByKeyword(String username, String keyword, Pageable pageable) {
        Page<Taches> pageTaches = findTachesByAnnotateurAndKeyword(username, keyword, pageable);
        return calculerAvancementTaches(pageTaches.getContent());
    }

    private List<Map<String, Object>> calculerAvancementTaches(List<Taches> taches) {
        List<Map<String, Object>> tachesAvecAvancement = new ArrayList<>();
        for (Taches tache : taches) {
            Map<String, Object> tacheInfo = new HashMap<>();
            tacheInfo.put("tache", tache);

            List<CoupleTexte> coupleTextes = tache.getCoupleTexte();
            long totalTextes = coupleTextes.size();
            long textesAnnotes = 0;
            Integer premierNonAnnoteIndex = null;

            for (int i = 0; i < coupleTextes.size(); i++) {
                CoupleTexte ct = coupleTextes.get(i);
                if (ct.getAnnotations() != null) {
                    textesAnnotes++;
                } else if (premierNonAnnoteIndex == null) {
                    premierNonAnnoteIndex = i;
                }
            }

            // Si tous les textes sont annotés, on prend le premier texte (index 0)
            if (premierNonAnnoteIndex == null && !coupleTextes.isEmpty()) {
                premierNonAnnoteIndex = 0;
            }

            int pourcentage = totalTextes > 0 ? (int) ((textesAnnotes * 100) / totalTextes) : 0;
            tacheInfo.put("pourcentage", pourcentage);
            tacheInfo.put("nbTextesAnnotes", textesAnnotes);  
            tacheInfo.put("nbTextes", totalTextes);  
            tacheInfo.put("premierNonAnnoteIndex", premierNonAnnoteIndex);
            tacheInfo.put("termine", textesAnnotes == totalTextes && totalTextes > 0);

            tachesAvecAvancement.add(tacheInfo);
        }
        return tachesAvecAvancement;
    }
    @Override
    public List<Taches> getTachesActive() {
        return tacheRepository.findByIsFinishedFalse();
    }

    @Override
    public void updateOverdueTasks() {
        Date today = new Date(); // date actuelle
        List<Taches> overdueTasks = tacheRepository.findByIsFinishedFalseAndDateLimiteBefore(today);

        for (Taches t : overdueTasks) {
            t.setFinished(true); // boolean = true
        }

        tacheRepository.saveAll(overdueTasks);
    }
    @Override
    public long countTachesEnCoursByAnnotateur(String username) {
        return tacheRepository.countByAnnotateurLoginAndIsFinishedFalse(username);
    }

    @Override
    public long countTachesTermineesByAnnotateur(String username) {
        return tacheRepository.countByAnnotateurLoginAndIsFinishedTrue(username);
    }

    @Override
    public long countTextesAnnotesByAnnotateur(String username) {
        return coupleTexteRepository.countByTachesAnnotateurLoginAndAnnotationsIsNotNull(username);
    }
}