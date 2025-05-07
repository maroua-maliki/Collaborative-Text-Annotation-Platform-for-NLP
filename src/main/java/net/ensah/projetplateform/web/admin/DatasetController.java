package net.ensah.projetplateform.web.admin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import net.ensah.projetplateform.entities.ClassePossible;
import net.ensah.projetplateform.entities.CoupleTexte;
import net.ensah.projetplateform.entities.Dataset;
import net.ensah.projetplateform.services.DatasetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
public class DatasetController {

    @Autowired
    private DatasetService datasetService;

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
            Model model) {

        if (bindingResult.hasErrors()) {
            return "admin/Dataset/formDataset";
        }

        try {

            List<CoupleTexte> couplesTexte = traiterFichierDataset(file, dataset);

            List<ClassePossible> classes = new ArrayList<>();
            if (classesPossibles != null && !classesPossibles.isEmpty()) {
                String[] classesArray = classesPossibles.split(",");
                for (String classe : classesArray) {
                    ClassePossible cp = new ClassePossible();
                    cp.setNomClasse(classe.trim());
                    cp.setDataset(dataset);
                    classes.add(cp);
                }
            }

            datasetService.saveDatasetWithData(dataset, couplesTexte, classes, file);

            return "redirect:/admin/home";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Erreur lors du traitement du fichier: " + e.getMessage());
            return "admin/Dataset/formDataset";
        }
    }

    private List<CoupleTexte> traiterFichierDataset(MultipartFile file, Dataset dataset) throws IOException {
        List<CoupleTexte> coupleTextes = new ArrayList<>();

        // Déterminer le type de fichier
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        dataset.setTypeFichier(fileExtension);

        if ("csv".equals(fileExtension)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
            String line;
            boolean isHeader = true;

            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                String[] data = line.split(",");
                if (data.length >= 2) {
                    CoupleTexte coupleTexte = new CoupleTexte();

                    coupleTexte.setTexte1(data[0].trim());
                    coupleTexte.setTexte2(data[1].trim());

                    coupleTexte.setDataset(dataset);
                    coupleTextes.add(coupleTexte);
                }
            }
        }

        String uploadDir = "uploads/datasets/";
        String filePath = uploadDir + UUID.randomUUID() + "_" + fileName;

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path path = Paths.get(filePath);
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        dataset.setCheminFichierExport(filePath);

        return coupleTextes;
    }
}