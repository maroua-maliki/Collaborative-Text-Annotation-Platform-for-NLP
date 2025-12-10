package net.ensah.projetplateform.services;

import net.ensah.projetplateform.entities.Role;
import net.ensah.projetplateform.entities.Utilisateur;
import net.ensah.projetplateform.repository.UtilisateurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UtilisateurServiceTest {

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @InjectMocks
    private UtilisateurService utilisateurService;

    private Utilisateur utilisateur;
    private Role role;

    @BeforeEach
    void setUp() {
        // Initialiser un rôle
        role = new Role();
        role.setId(1L);
        role.setNomRole("ADMIN");

        // Initialiser un utilisateur
        utilisateur = new Utilisateur();
        utilisateur.setId(1L);
        utilisateur.setNom("Dupont");
        utilisateur.setPrenom("Jean");
        utilisateur.setLogin("jdupont");
        utilisateur.setPassword("password123");
        utilisateur.setRole(role);
    }

    @Test
    void testLoadUserByUsername_Success() {
        // Arrange
        when(utilisateurRepository.findByLogin("jdupont")).thenReturn(utilisateur);

        // Act
        UserDetails userDetails = utilisateurService.loadUserByUsername("jdupont");

        // Assert
        assertNotNull(userDetails);
        assertEquals("jdupont", userDetails.getUsername());
        assertEquals("password123", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        // Arrange
        when(utilisateurRepository.findByLogin(anyString())).thenReturn(null);

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            utilisateurService.loadUserByUsername("unknown");
        });
    }

    @Test
    void testSimple() {
        // Un test simple pour vérifier le fonctionnement de base
        assertEquals(2, 1+1, "1 + 1 doit être égal à 2");
    }
}