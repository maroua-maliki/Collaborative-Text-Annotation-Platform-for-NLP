package net.ensah.projetplateform.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@AllArgsConstructor @NoArgsConstructor @Setter @Getter
public class Dataset {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nomDataset;
    private String description;
    @OneToMany(mappedBy = "dataset")
    private List<ClassePossible> classePossible;
    @OneToMany(mappedBy = "dataset")
    private List<Taches> taches;
    @OneToMany(mappedBy = "dataset")
    private List<CoupleTexte> coupleTexte;
}
