package net.ensah.projetplateform;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.TestPropertySource;

// Importation de tous vos repositories
import net.ensah.projetplateform.repository.*;

@SpringJUnitConfig
@TestPropertySource(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration"
})
class ProjetPlateformApplicationTests {

    @Mock
    private AnnotateurRepository annotateurRepository;

    @Mock
    private AnnotationsRepository annotationsRepository;

    @Mock
    private ClassePossibleRepository classePossibleRepository;

    @Mock
    private CoupleTexteRepository coupleTexteRepository;

    @Mock
    private DatasetRepository datasetRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private TacheRepository tacheRepository;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Test
    void contextLoads() {
        // Avec tous ces Mocks, Spring va démarrer sans chercher MySQL
    }
}