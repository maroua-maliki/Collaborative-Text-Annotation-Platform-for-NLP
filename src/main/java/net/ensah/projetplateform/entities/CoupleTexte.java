package net.ensah.projetplateform.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor @NoArgsConstructor @Setter @Getter
public class CoupleTexte {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long texte1;
    private Long texte2;
    @ManyToOne
    private Taches taches;
    @OneToOne
    private Annotations annotations;
    @ManyToOne
    private Dataset dataset;
}
