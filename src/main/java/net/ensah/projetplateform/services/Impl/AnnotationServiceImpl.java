package net.ensah.projetplateform.services.Impl;

import net.ensah.projetplateform.entities.Annotations;
import net.ensah.projetplateform.entities.Annotateur;
import net.ensah.projetplateform.entities.ClassePossible;
import net.ensah.projetplateform.entities.CoupleTexte;
import net.ensah.projetplateform.repository.AnnotationsRepository;
import net.ensah.projetplateform.repository.AnnotateurRepository;
import net.ensah.projetplateform.repository.ClassePossibleRepository;
import net.ensah.projetplateform.repository.CoupleTexteRepository;
import net.ensah.projetplateform.services.AnnotationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AnnotationServiceImpl implements AnnotationService {

    @Autowired
    private AnnotationsRepository annotationsRepository;

    @Autowired
    private CoupleTexteRepository coupleTexteRepository;

    @Autowired
    private AnnotateurRepository annotateurRepository;

    @Autowired
    private ClassePossibleRepository classePossibleRepository;

    @Override
    @Transactional
    public Annotations saveAnnotation(Long coupleTexteId, Long classeId, String username) {

        Annotateur annotateur = annotateurRepository.findByLogin(username);
        if (annotateur == null) {
            throw new RuntimeException("Annotateur non trouvé");
        }

        CoupleTexte coupleTexte = coupleTexteRepository.findById(coupleTexteId)
                .orElseThrow(() -> new RuntimeException("Couple de texte non trouvé"));

        ClassePossible classe = classePossibleRepository.findById(classeId)
                .orElseThrow(() -> new RuntimeException("Classe non trouvée"));

        Annotations annotation = coupleTexte.getAnnotations();
        if (annotation == null) {
            annotation = new Annotations();
            annotation.setCoupleTexte(coupleTexte);
        }

        annotation.setClasseChoisie(classe.getNomClasse());
        annotation.setAnnotateur(annotateur);

        annotation = annotationsRepository.save(annotation);

        coupleTexte.setAnnotations(annotation);
        coupleTexteRepository.save(coupleTexte);

        return annotation;
    }

    @Override
    public Annotations getAnnotationByCoupleTexteId(Long coupleTexteId) {
        CoupleTexte coupleTexte = coupleTexteRepository.findById(coupleTexteId)
                .orElseThrow(() -> new RuntimeException("Couple de texte non trouvé"));
        return coupleTexte.getAnnotations();
    }

    @Override
    public ClassePossible getClassePossibleById(Long classeId) {
        return classePossibleRepository.findById(classeId)
                .orElseThrow(() -> new RuntimeException("Classe non trouvée"));
    }

    @Override
    @Transactional
    public Annotations updateAnnotation(Long coupleTexteId, Long classeId) {
        CoupleTexte coupleTexte = coupleTexteRepository.findById(coupleTexteId)
                .orElseThrow(() -> new RuntimeException("Couple de texte non trouvé"));
        
        ClassePossible classe = classePossibleRepository.findById(classeId)
                .orElseThrow(() -> new RuntimeException("Classe non trouvée"));
        
        Annotations annotation = coupleTexte.getAnnotations();
        if (annotation == null) {
            annotation = new Annotations();
            annotation.setCoupleTexte(coupleTexte);
            // Conserver l'annotateur existant ou null si c'est une nouvelle annotation
        }
        
        annotation.setClasseChoisie(classe.getNomClasse());
        
        annotation = annotationsRepository.save(annotation);
        coupleTexte.setAnnotations(annotation);
        coupleTexteRepository.save(coupleTexte);
        
        return annotation;
    }

    @Override
    public List<Annotations> getAllAnnotations() {
        return annotationsRepository.findAll();
    }
}
