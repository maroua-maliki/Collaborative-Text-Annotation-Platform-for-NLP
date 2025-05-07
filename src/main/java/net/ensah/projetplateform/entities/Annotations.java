package net.ensah.projetplateform.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor @NoArgsConstructor @Setter @Getter @ToString
public class Annotations {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String classeChoisie;
    @OneToOne(mappedBy = "annotations")
    private CoupleTexte coupleTexte;
    @ManyToOne
    private Annotateur annotateur;
}
