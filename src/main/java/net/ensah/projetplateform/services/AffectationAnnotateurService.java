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
}
