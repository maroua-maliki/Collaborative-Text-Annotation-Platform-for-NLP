package net.ensah.projetplateform.web.admin;

import jakarta.validation.Valid;
import net.ensah.projetplateform.entities.Annotateur;
import net.ensah.projetplateform.entities.ClassePossible;
import net.ensah.projetplateform.entities.CoupleTexte;
import net.ensah.projetplateform.entities.Dataset;
import net.ensah.projetplateform.services.AffectationAnnotateurService;
import net.ensah.projetplateform.services.AsyncDatasetService;
import net.ensah.projetplateform.services.DatasetService;
import net.ensah.projetplateform.services.AnnotateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Date;
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

     @Autowired
     private AffectationAnnotateurService affectationAnnotateurService;

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

            Dataset savedDataset = datasetService.createDataset(dataset.getNomDataset(), dataset.getDescription(), file, classesPossibles);

            datasetService.saveDataset(savedDataset);

            asyncDatasetService.parseDatasetAsync(savedDataset);

            redirectAttributes.addFlashAttribute("success", "Dataset added successfully");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to upload dataset: " + e.getMessage());
            return "redirect:/admin/dataset/list";
        }

        return "redirect:/admin/dataset/list";
    }

    @GetMapping("/dataset/{id}")
    public String showDataset(@PathVariable("id") Long id,
                              @RequestParam(name = "page", defaultValue = "0") int page,
                              @RequestParam(name = "size", defaultValue = "10") int size,
                              Model model) {
        Dataset dataset = datasetService.getDatasetById(id);
        if (dataset == null) {
            throw new RuntimeException("Dataset introuvable");
        }

        // Récupérer les couples de texte avec pagination
        Page<CoupleTexte> coupleTextsPage = datasetService.getCoupleTextsByDatasetId(id, page, size);

        // Calcul pour la fenêtre de pagination (afficher 5 pages maximum, centrées sur la page courante)
        int totalPages = coupleTextsPage.getTotalPages();
        int currentPage = page;

        List<ClassePossible> classePossibles = dataset.getClassePossible();

        // Ajouter les attributs au modèle
        model.addAttribute("dataset", dataset);
        model.addAttribute("taille", dataset.getCoupleTexte().size());
        model.addAttribute("tailleClasse", classePossibles.size());
        model.addAttribute("coupleTexte", coupleTextsPage.getContent()); // Utiliser le contenu de la page
        model.addAttribute("classePossibles", classePossibles);

        // Ajouter les attributs de pagination
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", coupleTextsPage.getTotalElements());
        model.addAttribute("pageSize", size);

        return "admin/Dataset/detaillsDataset";
    }

    @GetMapping("/dataset/ajouter-annotateur/{id}")
    public String ajouterAnnotateur(@PathVariable("id") Long id, Model model) {

        Dataset dataset = datasetService.getDatasetById(id);

        List<Annotateur> annotateurs = annotateurService.getActiveAnnotateurs();

        model.addAttribute("annotateurs", annotateurs);
        model.addAttribute("datasetId", id);
        model.addAttribute("datasetName", dataset.getNomDataset());

        return "admin/Dataset/listUser";
    }

    @PostMapping("/dataset/affecter-annotateurs")
    public String affecterAnnotateurs(
            @RequestParam("datasetId") Long datasetId,
            @RequestParam(value = "selectedAnnotateurs", required = false) List<Long> annotateursIds,
            @RequestParam("dateLimite") @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateLimite,
            RedirectAttributes redirectAttributes) {

        if (annotateursIds == null || annotateursIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Veuillez sélectionner au moins un annotateur.");
            return "redirect:/admin/dataset/ajouter-annotateur/" + datasetId;
        }

        List<Annotateur> annotateurs = annotateurService.findAnnotateursByIds(annotateursIds);

        affectationAnnotateurService.ajouterAnnotateur(datasetId, annotateurs, dateLimite);

        redirectAttributes.addFlashAttribute("success", "Les annotateurs ont été affectés avec succès.");
        return "redirect:/admin/dataset/" + datasetId;
    }


}