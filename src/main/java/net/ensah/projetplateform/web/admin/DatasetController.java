package net.ensah.projetplateform.web.admin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import net.ensah.projetplateform.entities.ClassePossible;
import net.ensah.projetplateform.entities.CoupleTexte;
import net.ensah.projetplateform.entities.Dataset;
import net.ensah.projetplateform.services.DatasetService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
public class DatasetController {

    @Autowired
    private DatasetService datasetService;

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

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        dataset.setTypeFichier(fileExtension);

        if ("csv".equals(fileExtension)) {
            // Traitement des fichiers CSV
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
        } else if ("xlsx".equals(fileExtension)) {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            if (rows.hasNext()) {
                rows.next();
            }

            while (rows.hasNext()) {
                Row row = rows.next();
                if (row.getCell(0) != null && row.getCell(1) != null) {
                    CoupleTexte coupleTexte = new CoupleTexte();

                    Cell cell1 = row.getCell(0);
                    Cell cell2 = row.getCell(1);

                    coupleTexte.setTexte1(getCellValueAsString(cell1));
                    coupleTexte.setTexte2(getCellValueAsString(cell2));
                    coupleTexte.setDataset(dataset);
                    coupleTextes.add(coupleTexte);
                }
            }
            workbook.close();
        }

        String uploadDir = "src/main/resources/static/uploads/datasets/";
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

    private String getCellValueAsString(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue()).trim();
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue()).trim();
            default:
                return "";
        }


    }
}