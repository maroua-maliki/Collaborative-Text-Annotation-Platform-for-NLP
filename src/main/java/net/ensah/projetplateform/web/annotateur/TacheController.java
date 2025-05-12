package net.ensah.projetplateform.web.annotateur;

import net.ensah.projetplateform.entities.Annotations;
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
import java.util.Map;


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
        List<Map<String, Object>> tachesAvecAvancement;

        if (keyword != null && !keyword.isEmpty()) {
            pageTaches = tacheService.findTachesByAnnotateurAndKeyword(username, keyword, PageRequest.of(page, size));
            tachesAvecAvancement = tacheService.getTachesAvecAvancementByKeyword(username, keyword, PageRequest.of(page, size));
        } else {
            pageTaches = tacheService.findTachesByAnnotateur(username, PageRequest.of(page, size));
            tachesAvecAvancement = tacheService.getTachesAvecAvancement(username, PageRequest.of(page, size));
        }

        model.addAttribute("listeTaches", tachesAvecAvancement);
        model.addAttribute("pages", new int[pageTaches.getTotalPages()]);
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", keyword);

        return "user/listTache";
    }

    @GetMapping("/tache/textes")
    public String afficherTextes(Model model,
                                 @RequestParam("tacheId") Long tacheId,
                                 @RequestParam("textIndex") int textIndex) {

        Taches tache = tacheService.getTacheById(tacheId);

        List<CoupleTexte> coupleTextes = tacheService.getCoupleTextesByTacheId(tacheId);
        CoupleTexte coupleTexte = null;

        if (!coupleTextes.isEmpty()) {
            if (textIndex < 0) textIndex = 0;
            if (textIndex >= coupleTextes.size()) textIndex = coupleTextes.size() - 1;

            coupleTexte = coupleTextes.get(textIndex);

            // Récupérer l'annotation existante si elle existe
            Annotations existingAnnotation = coupleTexte.getAnnotations();
            if (existingAnnotation != null) {
                model.addAttribute("existingAnnotation", existingAnnotation);
            }
        }

        List<ClassePossible> classesPossibles = tache.getDataset().getClassePossible();

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