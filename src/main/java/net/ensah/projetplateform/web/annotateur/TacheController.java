package net.ensah.projetplateform.web.annotateur;

import net.ensah.projetplateform.entities.Annotateur;
import net.ensah.projetplateform.entities.ClassePossible;
import net.ensah.projetplateform.entities.CoupleTexte;
import net.ensah.projetplateform.entities.Taches;
import net.ensah.projetplateform.services.TacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@Controller
@RequestMapping("/user")
public class TacheController {

    @Autowired
    private TacheService tacheService;

    @GetMapping("/tache/list")
    public String list(Model model,
                       @RequestParam(name = "page", defaultValue = "0") int page,
                       @RequestParam(name = "size", defaultValue = "5") int size,
                       @RequestParam(name = "keyword", defaultValue = "") String keyword) {

        // Récupérer l'utilisateur connecté
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Page<Taches> pageTaches;
        if (keyword != null && !keyword.isEmpty()) {
            pageTaches = tacheService.findTachesByAnnotateurAndKeyword(username, keyword, PageRequest.of(page, size));
        } else {
            pageTaches = tacheService.findTachesByAnnotateur(username, PageRequest.of(page, size));
        }

        model.addAttribute("listeTaches", pageTaches.getContent());
        model.addAttribute("pages", new int[pageTaches.getTotalPages()]);
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", keyword);

        return "user/listTache";
    }

    @GetMapping("/tache/textes")
    public String afficherTextes(Model model,
                                 @RequestParam("tacheId") Long tacheId,
                                 @RequestParam("textIndex") int textIndex) {

        // Récupérer la tâche
        Taches tache = tacheService.getTacheById(tacheId);

        // Récupérer tous les couples de texte de cette tâche
        List<CoupleTexte> coupleTextes = tacheService.getCoupleTextesByTacheId(tacheId);
        CoupleTexte coupleTexte = null;

        // Vérifier si l'index est valide
        if (!coupleTextes.isEmpty()) {
            // S'assurer que l'index est dans les limites
            if (textIndex < 0) textIndex = 0;
            if (textIndex >= coupleTextes.size()) textIndex = coupleTextes.size() - 1;

            coupleTexte = coupleTextes.get(textIndex);
        }

        // Récupérer les classes possibles du dataset
        List<ClassePossible> classesPossibles = tache.getDataset().getClassePossible();

        // Ajouter les attributs au modèle
        model.addAttribute("coupleTexte", coupleTexte);
        model.addAttribute("classesPossibles", classesPossibles);
        model.addAttribute("tacheId", tacheId);
        model.addAttribute("textIndex", textIndex);
        model.addAttribute("totalTextes", coupleTextes.size());
        model.addAttribute("hasPrevious", textIndex > 0);
        model.addAttribute("hasNext", textIndex < coupleTextes.size() - 1);
        model.addAttribute("datasetName", tache.getDataset().getNomDataset());

        return "user/listTexte";
    }
}