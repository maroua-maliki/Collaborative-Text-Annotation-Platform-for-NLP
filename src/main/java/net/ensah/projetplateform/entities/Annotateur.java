package net.ensah.projetplateform.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.List;

@Entity
@AllArgsConstructor @NoArgsConstructor @Setter @Getter @ToString
public class Annotateur extends Utilisateur{
    @OneToMany(mappedBy = "annotateur")
    private List<Taches> taches;
    @OneToMany(mappedBy = "annotateur")
    private List<Annotations> annotations;
}
