package net.ensah.projetplateform.services;

import net.ensah.projetplateform.entities.ClassePossible;
import net.ensah.projetplateform.entities.CoupleTexte;
import net.ensah.projetplateform.entities.Dataset;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface DatasetService {

    List<Dataset> getAllDatasets();

    Dataset getDatasetById(Long id);

    Page<CoupleTexte> getCoupleTexts(int page, int size);
    Page<CoupleTexte> getCoupleTextsByDatasetId(Long datasetId, int page, int size);

    void saveDataset(Dataset dataset);

    Dataset createDataset(String name, String description, MultipartFile file, String classesRaw);

    void parseDatasetFile(Dataset dataset) throws IOException;
}