package net.ensah.projetplateform.web.admin;

import net.ensah.projetplateform.services.StatistiqueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/admin")
public class StatistiqueController {


    
    @GetMapping("/statistics")
    public String showStatistics(Model model) {

        return "admin/statistique";
    }
}