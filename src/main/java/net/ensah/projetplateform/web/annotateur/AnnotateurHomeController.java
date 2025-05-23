package net.ensah.projetplateform.web.annotateur;

import net.ensah.projetplateform.services.TacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class AnnotateurHomeController {

    @Autowired
    private TacheService tacheService;

    @GetMapping("/home")
    public String home(Model model) {
        // Récupérer l'utilisateur connecté
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // Compter les tâches en cours
        long tachesEnCours = tacheService.countTachesEnCoursByAnnotateur(username);

        // Compter les tâches terminées
        long tachesTerminees = tacheService.countTachesTermineesByAnnotateur(username);

        // Compter le nombre total de textes annotés
        long textesAnnotes = tacheService.countTextesAnnotesByAnnotateur(username);

        // Ajouter les statistiques au modèle
        model.addAttribute("tachesEnCours", tachesEnCours);
        model.addAttribute("tachesTerminees", tachesTerminees);
        model.addAttribute("textesAnnotes", textesAnnotes);

        return "user/home";
    }
}