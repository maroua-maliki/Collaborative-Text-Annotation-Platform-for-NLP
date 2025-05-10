package net.ensah.projetplateform.services;

import net.ensah.projetplateform.entities.Annotations;
import net.ensah.projetplateform.entities.ClassePossible;
import net.ensah.projetplateform.entities.CoupleTexte;

public interface AnnotationService {
    Annotations saveAnnotation(Long coupleTexteId, Long classeId, String username);
    Annotations getAnnotationByCoupleTexteId(Long coupleTexteId);
    ClassePossible getClassePossibleById(Long classeId);
}