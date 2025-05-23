package net.ensah.projetplateform;

import net.ensah.projetplateform.services.TacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class TacheInitializer implements ApplicationRunner {

    @Autowired
    private TacheService tacheService;

    @Override
    public void run(ApplicationArguments args) {
        tacheService.updateOverdueTasks();
    }
}
