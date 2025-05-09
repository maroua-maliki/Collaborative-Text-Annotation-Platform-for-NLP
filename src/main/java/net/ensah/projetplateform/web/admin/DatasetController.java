package net.ensah.projetplateform.web.admin;

import jakarta.validation.Valid;
import net.ensah.projetplateform.entities.Annotateur;
import net.ensah.projetplateform.entities.ClassePossible;
import net.ensah.projetplateform.entities.CoupleTexte;
import net.ensah.projetplateform.entities.Dataset;
import net.ensah.projetplateform.services.AsyncDatasetService;
import net.ensah.projetplateform.services.DatasetService;
import net.ensah.projetplateform.services.AnnotateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class DatasetController {

    @Autowired
    private DatasetService datasetService;


    @Autowired
    private AsyncDatasetService asyncDatasetService;

     @Autowired
     private AnnotateurService annotateurService;

    @GetMapping("/dataset/list")
    public String listDatasets(Model model) {
        List<Dataset> datasets = datasetService.getAllDatasets();
        model.addAttribute("datasets", datasets);
        return "admin/Dataset/listDataset";
    }

    @GetMapping("/dataset/ajouter")
    public String showDatasetForm(Model model) {
        model.addAttribute("dataset", new Dataset());
        return "admin/Dataset/formDataset";
    }

    @PostMapping("/dataset/save")
    public String saveDataset(
            @Valid Dataset dataset,
            @RequestParam("datasetFile") MultipartFile file,
            @RequestParam("classesPossibles") String classesPossibles,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        try {

            // Assume this method creates the dataset, saves the file, and returns the saved Dataset entity
            Dataset savedDataset = datasetService.createDataset(dataset.getNomDataset(), dataset.getDescription(), file, classesPossibles);

            datasetService.saveDataset(savedDataset);

            // Trigger async parsing
            asyncDatasetService.parseDatasetAsync(savedDataset);

            redirectAttributes.addFlashAttribute("success", "Dataset added successfully");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to upload dataset: " + e.getMessage());
            return "redirect:/admin/dataset/list";
        }

        return "redirect:/admin/dataset/list";
    }

    @GetMapping("/dataset/{id}")
    public String showDataset(@PathVariable("id") Long id, Model model) {
        Dataset dataset = datasetService.getDatasetById(id);
        if (dataset == null) {
            throw new RuntimeException("Dataset introuvable");
        }
        List<CoupleTexte> coupleTexte = dataset.getCoupleTexte();
        Integer taille = coupleTexte.size();
        List<ClassePossible> classePossibles = dataset.getClassePossible();
        int tailleClasse = classePossibles.size();
        model.addAttribute("dataset", dataset);
        model.addAttribute("taille", taille);
        model.addAttribute("tailleClasse", tailleClasse);
        model.addAttribute("coupleTexte", coupleTexte);
        model.addAttribute("classePossibles", classePossibles);
        return "admin/Dataset/detaillsDataset";
    }


}