package net.ensah.projetplateform.web.admin;

import net.ensah.projetplateform.entities.Annotateur;
import net.ensah.projetplateform.entities.Annotations;
import net.ensah.projetplateform.entities.Taches;
import net.ensah.projetplateform.services.AnnotateurService;
import net.ensah.projetplateform.services.AnnotationService;
import net.ensah.projetplateform.services.TacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminHomeController {

    @Autowired
    private AnnotateurService annotateurService;
    
    @Autowired
    private TacheService tacheService;
    
    @Autowired
    private AnnotationService annotationService;

    @GetMapping({"", "/", "/home"})
    public String showDashboard(Model model) {
        // Récupérer le nombre total d'annotateurs actifs
        List<Annotateur> annotateurs = annotateurService.getActiveAnnotateurs();
        model.addAttribute("totalAnnotateurs", annotateurs.size());
        
        // Récupérer le nombre de tâches actives
        List<Taches> taches = tacheService.getTachesActive();
        model.addAttribute("tachesActives", taches.size());
        
        // Récupérer le nombre total d'annotations
        List<Annotations> annotations = annotationService.getAllAnnotations();
        model.addAttribute("totalAnnotations", annotations.size());
        
        return "admin/home";
    }
}