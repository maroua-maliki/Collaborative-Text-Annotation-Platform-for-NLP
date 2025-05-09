package net.ensah.projetplateform.services.Impl;

import net.ensah.projetplateform.entities.ClassePossible;
import net.ensah.projetplateform.entities.CoupleTexte;
import net.ensah.projetplateform.entities.Dataset;
import net.ensah.projetplateform.repository.ClassePossibleRepository;
import net.ensah.projetplateform.repository.CoupleTexteRepository;
import net.ensah.projetplateform.repository.DatasetRepository;
import net.ensah.projetplateform.services.DatasetService;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DatasetServiceImpl implements DatasetService {

    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/datasets";

    @Autowired
    private DatasetRepository datasetRepository;

    @Autowired
    private ClassePossibleRepository classePossibleRepository;

    @Autowired
    private CoupleTexteRepository coupleTextRepository;

    @Override
    public List<Dataset> getAllDatasets() {
        return datasetRepository.findAll();
    }

    @Override
    public Dataset getDatasetById(Long id) {
        return datasetRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void saveDataset(Dataset dataset) {
        datasetRepository.save(dataset);
    }

    @Override
    @Transactional
    public Dataset createDataset(String name, String description, MultipartFile file, String classesRaw) {

        Dataset dataset = new Dataset();
        dataset.setNomDataset(name);
        dataset.setDescription(description);

        if (file != null && !file.isEmpty()) {
            // Create upload directory if it doesn't exist
            File uploadDirFile = new File(UPLOAD_DIR);
            if (!uploadDirFile.exists()) {
                uploadDirFile.mkdirs();
            }

            // Generate a unique filename to avoid collisions
            String originalFilename = file.getOriginalFilename();
            String uniqueFilename = UUID.randomUUID() + "_" + originalFilename;

            // Create the complete file path
            Path targetLocation = Paths.get(UPLOAD_DIR, uniqueFilename).toAbsolutePath();

            // Actually save the file to disk
            try {
                Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Store the absolute path in the dataset
            dataset.setCheminFichierExport(targetLocation.toString());
            dataset.setTypeFichier(file.getContentType());
        }

        // Handle classes
        List<ClassePossible> classSet = Arrays.stream(classesRaw.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(className -> {
                    ClassePossible cp = new ClassePossible();
                    cp.setNomClasse(className);
                    cp.setDataset(dataset);
                    return cp;
                })
                .collect(Collectors.toList());

        System.out.println(classSet);
        dataset.setClassePossible(classSet);

        // Save the dataset to get an ID assigned
        return datasetRepository.save(dataset);
    }

    @Override
    public void parseDatasetFile(Dataset dataset) throws IOException {
        final int MAX_ROWS = 1000;

        String filename = dataset.getCheminFichierExport();
        if (filename == null || filename.isEmpty()) {
            throw new IllegalArgumentException("Dataset has no associated file");
        }

        Path filePath = Paths.get(filename);

        if (!Files.exists(filePath)) {
            // Try as a classpath resource
            try {
                File resourceFile = new File(filename);
                if (resourceFile.exists()) {
                    filePath = resourceFile.toPath();
                } else {
                    throw new RuntimeException("File not found: " + filename);
                }
            } catch (Exception e) {
                throw new RuntimeException("File not found: " + filename, e);
            }
        }

        List<CoupleTexte> coupleTexts = new ArrayList<>();

        // Déterminer le type de fichier par l'extension ou le type MIME
        String typeFichier = dataset.getTypeFichier();
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();

        if (extension.equals("csv") || (typeFichier != null && typeFichier.contains("csv"))) {
            // Traitement du fichier CSV
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
                String line;
                int rowCount = 0;

                // Sauter l'en-tête si présent
                boolean headerSkipped = false;

                while ((line = reader.readLine()) != null && rowCount < MAX_ROWS) {
                    if (!headerSkipped) {
                        headerSkipped = true;
                        continue;
                    }

                    // Diviser la ligne en colonnes
                    String[] columns = line.split(",");

                    if (columns.length >= 2) {
                        String text1 = columns[0].trim();
                        String text2 = columns[1].trim();

                        // Retirer les guillemets si présents
                        text1 = text1.replaceAll("^\"|\"$", "");
                        text2 = text2.replaceAll("^\"|\"$", "");

                        CoupleTexte couple = new CoupleTexte();
                        couple.setTexte1(text1);
                        couple.setTexte2(text2);
                        couple.setDataset(dataset);

                        coupleTexts.add(couple);
                        rowCount++;
                    }
                }

                coupleTextRepository.saveAll(coupleTexts);
            } catch (IOException e) {
                throw new RuntimeException("Erreur lors de la lecture du fichier CSV", e);
            }
        } else {
            // Traitement du fichier Excel (code existant)
            try (InputStream fileInputStream = new FileInputStream(filePath.toFile());
                 Workbook workbook = WorkbookFactory.create(fileInputStream)) {

                Sheet sheet = workbook.getSheetAt(0);
                Iterator<Row> rowIterator = sheet.iterator();

                int rowCount = 0;

                // Skip header
                if (rowIterator.hasNext()) {
                    rowIterator.next();
                }

                while (rowIterator.hasNext() && rowCount < MAX_ROWS) {
                    Row row = rowIterator.next();
                    Cell text1Cell = row.getCell(0);
                    Cell text2Cell = row.getCell(1);

                    if (text1Cell == null || text2Cell == null) continue;

                    String text1 = getCellValueAsString(text1Cell).trim();
                    String text2 = getCellValueAsString(text2Cell).trim();

                    CoupleTexte couple = new CoupleTexte();
                    couple.setTexte1(text1);
                    couple.setTexte2(text2);
                    couple.setDataset(dataset);

                    coupleTexts.add(couple);
                    rowCount++;
                }

                coupleTextRepository.saveAll(coupleTexts);
            } catch (IOException e) {
                throw new RuntimeException("Erreur lors de la lecture du fichier Excel", e);
            }
        }
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
}