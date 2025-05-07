package net.ensah.projetplateform.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Entity
@AllArgsConstructor @NoArgsConstructor @Setter @Getter @ToString
public class Taches {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Temporal( TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateLimite;
    @ManyToOne
    private Annotateur annotateur;
    @ManyToOne
    private Dataset dataset;
    @OneToMany(mappedBy = "taches")
    private List<CoupleTexte> coupleTexte;
}
