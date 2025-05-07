package net.ensah.projetplateform.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor @NoArgsConstructor @Setter @Getter @ToString
public class ClassePossible {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nomClasse;
    @ManyToOne
    private Dataset dataset;
}
