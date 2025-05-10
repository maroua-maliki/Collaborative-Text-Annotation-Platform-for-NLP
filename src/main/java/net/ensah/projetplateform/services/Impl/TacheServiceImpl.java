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

import java.util.List;

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
}