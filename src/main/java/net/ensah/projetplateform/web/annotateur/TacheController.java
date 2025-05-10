package net.ensah.projetplateform.web.annotateur;

import net.ensah.projetplateform.entities.Annotateur;
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
}