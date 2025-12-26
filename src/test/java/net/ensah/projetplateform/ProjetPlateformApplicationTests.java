package net.ensah.projetplateform;

import net.ensah.projetplateform.repository.TacheRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration"
})
class ProjetPlateformApplicationTests {

    // On crée un "faux" bean pour satisfaire les dépendances de vos services
    @MockBean
    private TacheRepository tacheRepository;

    @Test
    void contextLoads() {
        // Le contexte va démarrer car tacheRepository est maintenant "simulé"
    }
}