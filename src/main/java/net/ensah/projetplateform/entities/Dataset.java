package net.ensah.projetplateform.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@AllArgsConstructor @NoArgsConstructor @Setter @Getter @ToString
public class Dataset {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nomDataset;
    private String cheminFichierExport;
    private String typeFichier;
    private String description;
    @OneToMany(mappedBy = "dataset", fetch = FetchType.EAGER)
    private List<ClassePossible> classePossible;
    @OneToMany(mappedBy = "dataset", fetch = FetchType.EAGER)
    private List<Taches> taches;
    @OneToMany(mappedBy = "dataset", fetch = FetchType.EAGER)
    private List<CoupleTexte> coupleTexte;
}
