package net.ensah.projetplateform.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor @NoArgsConstructor @Setter @Getter
public class Annotations {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String classeChoisie;
    @OneToOne(mappedBy = "annotations")
    private CoupleTexte coupleTexte;
    @ManyToOne
    private Utilisateur utilisateur;
}
