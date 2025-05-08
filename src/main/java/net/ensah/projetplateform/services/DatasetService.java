package net.ensah.projetplateform.services;

import net.ensah.projetplateform.entities.ClassePossible;
import net.ensah.projetplateform.entities.CoupleTexte;
import net.ensah.projetplateform.entities.Dataset;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DatasetService {

    List<Dataset> getAllDatasets();

    void saveDataset(Dataset dataset);

    void saveDatasetWithData(Dataset dataset, List<CoupleTexte> coupleTextes, List<ClassePossible> classes, MultipartFile file);

}