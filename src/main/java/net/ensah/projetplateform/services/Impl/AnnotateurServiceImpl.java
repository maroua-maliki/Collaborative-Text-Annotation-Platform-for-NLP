package net.ensah.projetplateform.services.Impl;

import jakarta.transaction.Transactional;
import net.ensah.projetplateform.entities.Annotateur;
import net.ensah.projetplateform.entities.CoupleTexte;
import net.ensah.projetplateform.entities.Dataset;
import net.ensah.projetplateform.entities.Taches;
import net.ensah.projetplateform.repository.AnnotateurRepository;
import net.ensah.projetplateform.repository.DatasetRepository;
import net.ensah.projetplateform.repository.TacheRepository;
import net.ensah.projetplateform.services.AnnotateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnnotateurServiceImpl implements AnnotateurService {

    @Autowired
    private AnnotateurRepository annotateurRepository;

    @Autowired
    private TacheRepository tacheRepository;

    @Autowired
    private DatasetRepository datasetRepository;

    @Override
    public List<Annotateur> getActiveAnnotateurs() {
        return annotateurRepository.findByIsActiveTrue(Pageable.unpaged()).getContent();
    }

    @Override
    public List<Annotateur> findAnnotateursByIds(List<Long> ids) {
        return annotateurRepository.findAllById(ids).stream()
                .filter(annotateur -> annotateur.getIsActive() != null && annotateur.getIsActive())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void desactiverAnnotateur(Long annotateurId) {
        Annotateur annotateur = annotateurRepository.findById(annotateurId)
                .orElseThrow(() -> new RuntimeException("Annotateur non trouvé"));

        List<Taches> taches = annotateur.getTaches();

        for (Taches tache : taches) {
            Dataset dataset = tache.getDataset();

            List<CoupleTexte> couplesNonAnnotes = tache.getCoupleTexte().stream()
                    .filter(couple -> couple.getAnnotations() == null)
                    .collect(Collectors.toList());

            // Récupérer les couples déjà annotés
            List<CoupleTexte> couplesAnnotes = tache.getCoupleTexte().stream()
                    .filter(couple -> couple.getAnnotations() != null)
                    .collect(Collectors.toList());

            List<Taches> autresTaches = dataset.getTaches().stream()
                    .filter(t -> !t.getId().equals(tache.getId()) && t.getAnnotateur().getIsActive())
                    .collect(Collectors.toList());

            if (!autresTaches.isEmpty() && !couplesNonAnnotes.isEmpty()) {
                // Redistribuer les couples non annotés aux autres annotateurs
                Collections.shuffle(couplesNonAnnotes);

                int nombreTaches = autresTaches.size();
                int index = 0;

                for (CoupleTexte couple : couplesNonAnnotes) {
                    Taches autreTache = autresTaches.get(index % nombreTaches);
                    couple.setTaches(autreTache);
                    index++;
                }
            }

            // Pour les couples déjà annotés, on les détache de la tâche mais on conserve leurs annotations
            for (CoupleTexte couple : couplesAnnotes) {
                couple.setTaches(null);
            }

            // Supprimer la tâche
            tacheRepository.delete(tache);

            // Sauvegarder les modifications
            datasetRepository.save(dataset);
        }

        // Désactiver l'annotateur
        annotateur.setIsActive(false);
        annotateurRepository.save(annotateur);
    }
}