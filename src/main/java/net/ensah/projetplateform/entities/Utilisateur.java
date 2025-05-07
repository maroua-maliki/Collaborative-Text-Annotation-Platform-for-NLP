package net.ensah.projetplateform.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Entity
@AllArgsConstructor @NoArgsConstructor @Setter @Getter @ToString
@Inheritance(strategy = InheritanceType.JOINED)
public class Utilisateur {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    @Size(min = 4, max = 40)
    private String nom;
    @Size(min = 4, max = 40)
    private String prenom;
    @Column(unique = true)
    @Size(min = 4, max = 40)
    private String login;
    private String password;
    @ManyToOne
    private Role role;
}
