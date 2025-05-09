package net.ensah.projetplateform.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor @NoArgsConstructor @Setter @Getter @ToString
public class CoupleTexte {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "LONGTEXT")
    private String texte1;
    @Column(columnDefinition = "LONGTEXT")
    private String texte2;
    @ManyToOne
    private Taches taches;
    @OneToOne
    private Annotations annotations;
    @ManyToOne
    @JoinColumn(name="dataset_id")
    private Dataset dataset;
}
