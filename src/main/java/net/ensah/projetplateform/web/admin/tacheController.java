package net.ensah.projetplateform.web.admin;

import net.ensah.projetplateform.entities.Taches;
import net.ensah.projetplateform.repository.AnnotateurRepository;
import net.ensah.projetplateform.repository.DatasetRepository;
import net.ensah.projetplateform.repository.TacheRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class tacheController {

    @Autowired
    private TacheRepository tacheRepository;

    @Autowired
    private AnnotateurRepository annotateurRepository;


    @GetMapping("/tache/add")
    public String formTache(Model model) {
        model.addAttribute("tache",new Taches());
        return "admin/Tache/formTache";
    }

}
