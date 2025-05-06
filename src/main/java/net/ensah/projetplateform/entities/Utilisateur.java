package net.ensah.projetplateform.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@AllArgsConstructor @NoArgsConstructor @Setter @Getter
public class Utilisateur {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String nom;
    private String prenom;
    @Column(unique = true)
    private String login;
    private String password;
    @OneToOne
    private Role role;
    @OneToMany(mappedBy = "utilisateur")
    private List<Taches> taches;
    @OneToMany(mappedBy = "utilisateur")
    private List<Annotations> annotations;
}
