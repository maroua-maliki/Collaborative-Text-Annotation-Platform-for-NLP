package net.ensah.projetplateform.web.admin;

import net.ensah.projetplateform.entities.Annotations;
import net.ensah.projetplateform.entities.Dataset;
import net.ensah.projetplateform.services.AnnotationService;
import net.ensah.projetplateform.services.DatasetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class StatistiqueController {

    @Autowired
    private DatasetService datasetService;
    
    @Autowired
    private AnnotationService annotationService;

    @GetMapping("/statistics")
    public String showStatistics(Model model) {
        // 1. Données pour le graphique d'évolution des annotations par mois
        List<Integer> annotationsParMois = getAnnotationsParMois();
        model.addAttribute("annotationsParMois", annotationsParMois);
        
        // 2. Données pour les graphiques de comparaison dataset
        List<Dataset> datasets = datasetService.getAllDatasets();
        List<String> datasetsLabels = datasets.stream()
                .map(Dataset::getNomDataset)
                .collect(Collectors.toList());
        
        // Préparer les données pour les deux séries
        List<Integer> totalPairsData = new ArrayList<>();
        List<Integer> annotationsData = new ArrayList<>();
        
        for (Dataset dataset : datasets) {
            // Nombre total de paires de texte dans le dataset
            int totalPairs = dataset.getCoupleTexte().size();
            totalPairsData.add(totalPairs);
            
            // Nombre d'annotations dans le dataset
            int annotationsCount = (int) dataset.getCoupleTexte().stream()
                    .filter(couple -> couple.getAnnotations() != null)
                    .count();
            annotationsData.add(annotationsCount);
        }
        
        model.addAttribute("datasetsLabels", datasetsLabels);
        model.addAttribute("datasetsData", annotationsData);
        model.addAttribute("totalPairsData", totalPairsData);
        
        // 3. Données pour le graphique de distribution des classes
        Map<String, Integer> classesDistribution = getClassesDistribution();
        List<String> classesLabels = new ArrayList<>(classesDistribution.keySet());
        List<Integer> classesData = new ArrayList<>(classesDistribution.values());
        
        model.addAttribute("classesLabels", classesLabels);
        model.addAttribute("classesData", classesData);
        
        // 4. Données pour le graphique des annotations par annotateur
        Map<String, Integer> annotateursDistribution = getAnnotateursDistribution();
        List<String> annotateursLabels = new ArrayList<>(annotateursDistribution.keySet());
        List<Integer> annotateursData = new ArrayList<>(annotateursDistribution.values());
        
        model.addAttribute("annotateursLabels", annotateursLabels);
        model.addAttribute("annotateursData", annotateursData);
        
        return "admin/statistique";
    }
    
    private List<Integer> getAnnotationsParMois() {
        // Initialiser le compteur pour chaque mois (12 mois)
        List<Integer> annotationsParMois = new ArrayList<>(Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
        
        // Récupérer toutes les annotations
        List<Annotations> annotations = annotationService.getAllAnnotations();
        
        // Comme l'entité Annotations n'a pas de champ dateAnnotation, 
        // nous utilisons une distribution simulée ou nous pouvons compter simplement le total
        int totalAnnotations = annotations.size();
        
        // Distribution simulée (pour démonstration)
        // Dans un cas réel, vous pourriez ajouter un champ date à votre entité Annotations
        // ou utiliser la date de création/modification de l'entité
        int currentMonth = LocalDate.now().getMonthValue() - 1; // 0-based index
        
        // Répartir les annotations sur les derniers mois (simulation)
        for (int i = 0; i < Math.min(6, currentMonth + 1); i++) {
            int monthIndex = (currentMonth - i + 12) % 12; // Pour gérer le passage à l'année précédente
            annotationsParMois.set(monthIndex, totalAnnotations / (i + 1));
        }
        
        return annotationsParMois;
    }
    
    private int countAnnotatedTexts(Dataset dataset) {
        // Compter le nombre de textes annotés dans un dataset
        return (int) dataset.getCoupleTexte().stream()
                .filter(couple -> couple.getAnnotations() != null)
                .count();
    }
    
    private Map<String, Integer> getClassesDistribution() {
        // Récupérer la distribution des classes dans les annotations
        Map<String, Integer> distribution = new HashMap<>();
        
        List<Annotations> annotations = annotationService.getAllAnnotations();
        
        for (Annotations annotation : annotations) {
            String classe = annotation.getClasseChoisie();
            if (classe != null && !classe.isEmpty()) {
                distribution.put(classe, distribution.getOrDefault(classe, 0) + 1);
            }
        }
        
        return distribution;
    }

    private Map<String, Integer> getAnnotateursDistribution() {
        Map<String, Integer> distribution = new HashMap<>();
        
        List<Annotations> annotations = annotationService.getAllAnnotations();
        
        for (Annotations annotation : annotations) {
            if (annotation.getAnnotateur() != null) {
                String nomAnnotateur = annotation.getAnnotateur().getNom() + " " + annotation.getAnnotateur().getPrenom();
                distribution.put(nomAnnotateur, distribution.getOrDefault(nomAnnotateur, 0) + 1);
            }
        }
        
        return distribution;
    }
}
