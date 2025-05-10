package net.ensah.projetplateform.services;

import net.ensah.projetplateform.entities.Annotateur;


import java.util.List;

public interface AnnotateurService {
     List<Annotateur> getActiveAnnotateurs();
     List<Annotateur> findAnnotateursByIds(List<Long> ids);

}
