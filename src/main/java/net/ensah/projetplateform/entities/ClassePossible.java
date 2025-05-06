package net.ensah.projetplateform.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor @NoArgsConstructor @Setter @Getter
public class ClassePossible {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long textClasse;
    @ManyToOne
    private Dataset dataset;
}
