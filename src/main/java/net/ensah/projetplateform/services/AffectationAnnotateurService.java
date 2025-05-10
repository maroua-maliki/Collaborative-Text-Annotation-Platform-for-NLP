package net.ensah.projetplateform.services;

import jakarta.transaction.Transactional;
import net.ensah.projetplateform.entities.Annotateur;
import net.ensah.projetplateform.entities.CoupleTexte;
import net.ensah.projetplateform.entities.Dataset;
import net.ensah.projetplateform.entities.Taches;
import net.ensah.projetplateform.repository.DatasetRepository;
import net.ensah.projetplateform.repository.TacheRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AffectationAnnotateurService {
    @Autowired
    private DatasetRepository datasetRepository;

    @Autowired
    private TacheRepository tacheRepository;

    @Transactional
    public void ajouterAnnotateur(Long datasetId, List<Annotateur> annotateurs, Date dateLimite) {

        Dataset dataset = datasetRepository.findById(datasetId)
                .orElseThrow(() -> new RuntimeException("Dataset non trouvé"));

        List<CoupleTexte> couplesTexte = new ArrayList<>(dataset.getCoupleTexte());
        Collections.shuffle(couplesTexte);

        int nombreAnnotateurs = annotateurs.size();
        int nombreCouplesParAnnotateur = couplesTexte.size() / nombreAnnotateurs;

        for (int i = 0; i < nombreAnnotateurs; i++) {
            Annotateur annotateur = annotateurs.get(i);

            Taches tache = new Taches();
            tache.setAnnotateur(annotateur);
            tache.setDataset(dataset);
            tache.setDateLimite(dateLimite);

            tache = tacheRepository.save(tache);


            int debut = i * nombreCouplesParAnnotateur;
            int fin = (i == nombreAnnotateurs - 1) ? couplesTexte.size() : (i + 1) * nombreCouplesParAnnotateur;

            for (int j = debut; j < fin; j++) {
                CoupleTexte couple = couplesTexte.get(j);
                couple.setTaches(tache);
            }
        }

        datasetRepository.save(dataset);
    }
    @Transactional
    public void supprimerAnnotateur(Long tacheId, Long datasetId) {
        // Récupérer la tâche et le dataset
        Taches tache = tacheRepository.findById(tacheId)
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée"));
        Dataset dataset = datasetRepository.findById(datasetId)
                .orElseThrow(() -> new RuntimeException("Dataset non trouvé"));

        // Récupérer tous les couples de texte de l'annotateur à supprimer
        List<CoupleTexte> couplesNonAnnotes = tache.getCoupleTexte().stream()
                .filter(couple -> couple.getAnnotations() == null)
                .collect(Collectors.toList());

        // Récupérer les couples déjà annotés (pour les conserver)
        List<CoupleTexte> couplesAnnotes = tache.getCoupleTexte().stream()
                .filter(couple -> couple.getAnnotations() != null)
                .collect(Collectors.toList());

        // Récupérer les autres annotateurs actifs du dataset
        List<Taches> autresTaches = dataset.getTaches().stream()
                .filter(t -> !t.getId().equals(tacheId))
                .collect(Collectors.toList());

        if (!autresTaches.isEmpty() && !couplesNonAnnotes.isEmpty()) {
            // Redistribuer les couples non annotés aux autres annotateurs
            Collections.shuffle(couplesNonAnnotes); // Mélanger pour distribution aléatoire

            int nombreTaches = autresTaches.size();
            int index = 0;

            for (CoupleTexte couple : couplesNonAnnotes) {
                // Affecter le couple à un autre annotateur
                Taches autreTache = autresTaches.get(index % nombreTaches);
                couple.setTaches(autreTache);

                index++;
            }
        }

        // Pour les couples déjà annotés, on les détache de la tâche mais on conserve leurs annotations
        for (CoupleTexte couple : couplesAnnotes) {
            // On conserve l'annotation mais on détache le couple de la tâche
            couple.setTaches(null);
        }

        // Supprimer la tâche
        tacheRepository.delete(tache);

        // Sauvegarder les modifications
        datasetRepository.save(dataset);
    }
}
