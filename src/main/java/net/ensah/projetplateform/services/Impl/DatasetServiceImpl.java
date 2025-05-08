package net.ensah.projetplateform.services.Impl;

import net.ensah.projetplateform.entities.ClassePossible;
import net.ensah.projetplateform.entities.CoupleTexte;
import net.ensah.projetplateform.entities.Dataset;
import net.ensah.projetplateform.repository.ClassePossibleRepository;
import net.ensah.projetplateform.repository.CoupleTexteRepository;
import net.ensah.projetplateform.repository.DatasetRepository;
import net.ensah.projetplateform.services.DatasetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class DatasetServiceImpl implements DatasetService {

    @Autowired
    private DatasetRepository datasetRepository;

    @Autowired
    private ClassePossibleRepository classePossibleRepository;

    @Autowired
    private CoupleTexteRepository coupleTexteRepository;

    @Override
    public List<Dataset> getAllDatasets() {
        return datasetRepository.findAll();
    }

    @Override
    @Transactional
    public void saveDataset(Dataset dataset) {
        datasetRepository.save(dataset);
    }

    @Override
    @Transactional
    public void saveDatasetWithData(Dataset dataset, List<CoupleTexte> coupleTextes,
                                    List<ClassePossible> classes, MultipartFile file) {

        Dataset savedDataset = datasetRepository.save(dataset);

        if (classes != null && !classes.isEmpty()) {
            for (ClassePossible cp : classes) {
                cp.setDataset(savedDataset);
                classePossibleRepository.save(cp);
            }
        }

        if (coupleTextes != null && !coupleTextes.isEmpty()) {
            for (CoupleTexte ct : coupleTextes) {
                ct.setDataset(savedDataset);
                coupleTexteRepository.save(ct);
            }
        }
    }


}