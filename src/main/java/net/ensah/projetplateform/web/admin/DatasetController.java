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

import java.util.*;
import java.util.stream.Collectors;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;

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
        List<Map<String, Object>> datasetsAvecAvancement = datasetService.getDatasetsWithProgress();
        model.addAttribute("datasetsInfo", datasetsAvecAvancement);
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

    @GetMapping("/dataset/supprimer-annotateur")
    public String supprimerAnnotateur(
            @RequestParam("tacheId") Long tacheId,
            @RequestParam("datasetId") Long datasetId,
            RedirectAttributes redirectAttributes) {

        try {
            affectationAnnotateurService.supprimerAnnotateur(tacheId, datasetId);
            redirectAttributes.addFlashAttribute("success", "L'annotateur a été supprimé du dataset avec succès. Les textes non annotés ont été redistribués.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression: " + e.getMessage());
        }

        return "redirect:/admin/dataset/" + datasetId;
    }

    @GetMapping("/dataset/delete/{id}")
    public String deleteDataset(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            datasetService.deleteDataset(id);
            redirectAttributes.addFlashAttribute("success", "Dataset supprimé avec succès");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression du dataset: " + e.getMessage());
        }
        return "redirect:/admin/dataset/list";
    }
    @GetMapping("/dataset/annotations/{id}")
    public String listAnnotations(@PathVariable("id") Long id,
                                  @RequestParam(name = "page", defaultValue = "0") int page,
                                  @RequestParam(name = "size", defaultValue = "10") int size,
                                  Model model) {
        Dataset dataset = datasetService.getDatasetById(id);
        if (dataset == null) {
            throw new RuntimeException("Dataset introuvable");
        }

        // Récupérer les couples de texte avec pagination
        Page<CoupleTexte> coupleTextsPage = datasetService.getCoupleTextsByDatasetId(id, page, size);

        // Ajouter les attributs au modèle
        model.addAttribute("dataset", dataset);
        model.addAttribute("coupleTexte", coupleTextsPage.getContent());

        // Ajouter les attributs de pagination
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", coupleTextsPage.getTotalPages());
        model.addAttribute("totalItems", coupleTextsPage.getTotalElements());
        model.addAttribute("pageSize", size);

        return "admin/Dataset/listAnnotation";
    }

    @GetMapping("/dataset/{id}/export/csv")
    public void exportToCsv(@PathVariable("id") Long id, HttpServletResponse response) throws IOException {
        Dataset dataset = datasetService.getDatasetById(id);
        if (dataset == null) {
            throw new RuntimeException("Dataset introuvable");
        }
        
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + dataset.getNomDataset() + "_annotations.csv\"");
        
        try (PrintWriter writer = response.getWriter()) {
            // Écrire l'en-tête
            writer.println("Texte1,Texte2,Annotation,Annotateur");
            
            // Écrire les données
            for (CoupleTexte couple : dataset.getCoupleTexte()) {
                String texte1 = couple.getTexte1().replace(",", " ").replace("\"", "'");
                String texte2 = couple.getTexte2().replace(",", " ").replace("\"", "'");
                String annotation = (couple.getAnnotations() != null) ? couple.getAnnotations().getClasseChoisie() : "Non annoté";
                String annotateur = (couple.getAnnotations() != null && couple.getAnnotations().getAnnotateur() != null) 
                    ? couple.getAnnotations().getAnnotateur().getNom() + " " + couple.getAnnotations().getAnnotateur().getPrenom() 
                    : "-";
                
                writer.println("\"" + texte1 + "\",\"" + texte2 + "\",\"" + annotation + "\",\"" + annotateur + "\"");
            }
        }
    }

    @GetMapping("/dataset/{id}/export/json")
    public void exportToJson(@PathVariable("id") Long id, HttpServletResponse response) throws IOException {
        Dataset dataset = datasetService.getDatasetById(id);
        if (dataset == null) {
            throw new RuntimeException("Dataset introuvable");
        }
        
        response.setContentType("application/json");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + dataset.getNomDataset() + "_annotations.json\"");
        
        List<Map<String, Object>> jsonData = new ArrayList<>();
        
        for (CoupleTexte couple : dataset.getCoupleTexte()) {
            Map<String, Object> item = new HashMap<>();
            item.put("texte1", couple.getTexte1());
            item.put("texte2", couple.getTexte2());
            item.put("annotation", (couple.getAnnotations() != null) ? couple.getAnnotations().getClasseChoisie() : null);
            
            if (couple.getAnnotations() != null && couple.getAnnotations().getAnnotateur() != null) {
                Map<String, String> annotateur = new HashMap<>();
                annotateur.put("nom", couple.getAnnotations().getAnnotateur().getNom());
                annotateur.put("prenom", couple.getAnnotations().getAnnotateur().getPrenom());
                item.put("annotateur", annotateur);
            } else {
                item.put("annotateur", null);
            }
            
            jsonData.add(item);
        }
        
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(response.getOutputStream(), jsonData);
    }
}
