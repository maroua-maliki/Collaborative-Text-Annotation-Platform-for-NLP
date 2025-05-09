package net.ensah.projetplateform.services.Impl;

import net.ensah.projetplateform.entities.Annotateur;
import net.ensah.projetplateform.repository.AnnotateurRepository;
import net.ensah.projetplateform.services.AnnotateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnnotateurServiceImpl implements AnnotateurService {

    @Autowired
    private AnnotateurRepository annotateurRepository;

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
}