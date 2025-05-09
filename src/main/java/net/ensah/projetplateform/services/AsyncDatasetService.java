package net.ensah.projetplateform.services;

import net.ensah.projetplateform.entities.Dataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AsyncDatasetService {

    @Autowired
    private DatasetService datasetService;

    @Async
    public void parseDatasetAsync(Dataset dataset) {
        try {
            datasetService.parseDatasetFile(dataset);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}