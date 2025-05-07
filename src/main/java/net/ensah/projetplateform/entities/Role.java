package net.ensah.projetplateform.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@AllArgsConstructor @NoArgsConstructor @Setter @Getter @ToString
public class Role {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nomRole;
    @OneToMany(mappedBy="role")
    private List<Utilisateur> utilisateur;
}
