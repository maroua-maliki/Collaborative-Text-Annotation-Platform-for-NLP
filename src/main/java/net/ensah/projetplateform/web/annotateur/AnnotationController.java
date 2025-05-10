package net.ensah.projetplateform.web.annotateur;

import net.ensah.projetplateform.services.AnnotationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user")
public class AnnotationController {

    @Autowired
    private AnnotationService annotationService;

    @PostMapping("/tache/saveAnnotation")
    public String saveAnnotation(
            @RequestParam("coupleTexteId") Long coupleTexteId,
            @RequestParam("tacheId") Long tacheId,
            @RequestParam("textIndex") int textIndex,
            @RequestParam("classeId") Long classeId,
            RedirectAttributes redirectAttributes) {

        try {
            // Récupérer l'utilisateur connecté
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUserName = authentication.getName();

            // Déléguer la logique métier au service
            annotationService.saveAnnotation(coupleTexteId, classeId, currentUserName);

            redirectAttributes.addFlashAttribute("success", "Annotation sauvegardée avec succès");

            // Rediriger vers le texte suivant
            return "redirect:/user/tache/textes?tacheId=" + tacheId + "&textIndex=" + (textIndex + 1);

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la sauvegarde: " + e.getMessage());
            return "redirect:/user/tache/textes?tacheId=" + tacheId + "&textIndex=" + textIndex;
        }
    }
}