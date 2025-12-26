package net.ensah.projetplateform;

import net.ensah.projetplateform.repository.TacheRepository;
import net.ensah.projetplateform.repository.CoupleTexteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration"
})
class ProjetPlateformApplicationTests {

    // Simulation du repository des tâches
    @MockBean
    private TacheRepository tacheRepository;

    // Simulation du repository des couples de texte (L'erreur venait d'ici !)
    @MockBean
    private CoupleTexteRepository coupleTexteRepository;

    @Test
    void contextLoads() {
        // Le contexte va maintenant démarrer car les dépendances sont satisfaites par les Mocks
    }
}