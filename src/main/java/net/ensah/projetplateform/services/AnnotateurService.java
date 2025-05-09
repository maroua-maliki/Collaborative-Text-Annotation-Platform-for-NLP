package net.ensah.projetplateform.services;

import net.ensah.projetplateform.entities.Annotateur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AnnotateurService {
     List<Annotateur> getActiveAnnotateurs();
     List<Annotateur> findAnnotateursByIds(List<Long> ids);
}