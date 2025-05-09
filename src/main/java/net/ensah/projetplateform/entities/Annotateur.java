package net.ensah.projetplateform.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Entity
@AllArgsConstructor @NoArgsConstructor @Setter @Getter @ToString
public class Annotateur extends Utilisateur {
    private Boolean isActive;

    @Email(message = "Format d'email invalide")
    @NotEmpty(message = "L'email est obligatoire")
    private String email;

    @OneToMany(mappedBy = "annotateur")
    private List<Taches> taches;

    @OneToMany(mappedBy = "annotateur")
    private List<Annotations> annotations;
}