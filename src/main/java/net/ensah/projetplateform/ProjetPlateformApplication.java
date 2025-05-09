package net.ensah.projetplateform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ProjetPlateformApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjetPlateformApplication.class, args);
    }

}
