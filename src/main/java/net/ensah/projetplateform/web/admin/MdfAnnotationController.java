package net.ensah.projetplateform.web.admin;

import net.ensah.projetplateform.entities.Annotations;
import net.ensah.projetplateform.entities.ClassePossible;
import net.ensah.projetplateform.entities.CoupleTexte;
import net.ensah.projetplateform.entities.Dataset;
import net.ensah.projetplateform.services.AnnotationService;
import net.ensah.projetplateform.services.DatasetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class MdfAnnotationController {

    @Autowired
    private DatasetService datasetService;
    
    @Autowired
    private AnnotationService annotationService;
    
    @GetMapping("/dataset/edit-annotation/{coupleId}")
    public String showEditAnnotationForm(@PathVariable("coupleId") Long coupleId,
                                         @RequestParam("datasetId") Long datasetId,
                                         Model model) {
        
        CoupleTexte coupleTexte = datasetService.getCoupleTexteById(coupleId);
        Dataset dataset = datasetService.getDatasetById(datasetId);
        List<ClassePossible> classesPossibles = dataset.getClassePossible();
        
        model.addAttribute("coupleTexte", coupleTexte);
        model.addAttribute("datasetId", datasetId);
        model.addAttribute("classesPossibles", classesPossibles);
        
        return "admin/Dataset/editAnnotation";
    }
    
    @PostMapping("/dataset/update-annotation")
    public String updateAnnotation(@RequestParam("coupleTexteId") Long coupleTexteId,
                                  @RequestParam("datasetId") Long datasetId,
                                  @RequestParam("classeId") Long classeId,
                                  RedirectAttributes redirectAttributes) {
        
        try {
            annotationService.updateAnnotation(coupleTexteId, classeId);
            redirectAttributes.addFlashAttribute("success", "Annotation modifiée avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la modification: " + e.getMessage());
        }
        
        return "redirect:/admin/dataset/annotations/" + datasetId;
    }
}
