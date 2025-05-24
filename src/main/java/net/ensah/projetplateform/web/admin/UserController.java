package net.ensah.projetplateform.web.admin;


import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import net.ensah.projetplateform.entities.Annotateur;
import net.ensah.projetplateform.entities.Role;
import net.ensah.projetplateform.repository.AnnotateurRepository;
import net.ensah.projetplateform.repository.RoleRepository;
import net.ensah.projetplateform.services.AnnotateurService;
import net.ensah.projetplateform.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class UserController {

    @Autowired
    private AnnotateurRepository annotateurRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AnnotateurService annotateurService;


    @GetMapping("/user/list")
    public String list(Model model,
                       @RequestParam(name = "page", defaultValue = "0") int page,
                       @RequestParam(name = "size", defaultValue = "5") int size,
                       @RequestParam(name = "keyword", defaultValue = "") String keyword) {


        Page<Annotateur> pageAnnotateur;
        if (keyword != null && !keyword.isEmpty()) {
            pageAnnotateur = annotateurRepository.findActiveAnnotateursByKeyword(keyword, PageRequest.of(page, size));
        } else {
            pageAnnotateur = annotateurRepository.findByIsActiveTrue(PageRequest.of(page, size));
        }

        model.addAttribute("listeAnnotateur", pageAnnotateur.getContent());
        model.addAttribute("pages", new int[pageAnnotateur.getTotalPages()]);
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", keyword);

        return "admin/GererUser/listUser";
    }

    @GetMapping("/user/delete")
    @Transactional
    public String delete(@RequestParam(name = "id") Long id, String keyword, int page) {
        annotateurService.desactiverAnnotateur(id);
        return "redirect:/admin/user/list?page="+page+"&keyword="+keyword;
    }

    @GetMapping("/annotateurs")
    @ResponseBody
    public List<Annotateur> listAnnotateur() {
        return annotateurRepository.findAll().stream()
                .filter(annotateur -> annotateur.getIsActive() != null && annotateur.getIsActive())
                .collect(Collectors.toList());
    }

    @GetMapping("/user/add")
    public String formAnnotateur(Model model){
        model.addAttribute("annotateur",new Annotateur());
        return "admin/GererUser/formUser";
    }

    @PostMapping(path="/user/save")
    public String save(Model model, @Valid Annotateur annotateur,
                       BindingResult bindingResult,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "") String keyword,
                       RedirectAttributes redirectAttributes) {

        // Vérification du login existant
        Annotateur existingAnnotateur = annotateurRepository.findByLogin(annotateur.getLogin());
        if (existingAnnotateur != null && (annotateur.getId() == null || !existingAnnotateur.getId().equals(annotateur.getId()))) {
            bindingResult.rejectValue("login", "error.annotateur", "Ce login est déjà utilisé");
        }
        
        // Vérification de l'email existant
        List<Annotateur> existingEmails = annotateurRepository.findByEmail(annotateur.getEmail());
        boolean emailExists = false;
        
        if (!existingEmails.isEmpty()) {
            // Si c'est une mise à jour, vérifier si l'email appartient à un autre utilisateur
            if (annotateur.getId() != null) {
                for (Annotateur existing : existingEmails) {
                    if (!existing.getId().equals(annotateur.getId())) {
                        emailExists = true;
                        break;
                    }
                }
            } else {
                // Si c'est une création, l'email existe déjà
                emailExists = true;
            }
        }
        
        if (emailExists) {
            bindingResult.rejectValue("email", "error.annotateur", "Cet email est déjà utilisé");
        }

        if(bindingResult.hasErrors()) {
            model.addAttribute("annotateur", annotateur);
            return "admin/GererUser/formUser";
        }

        String clearPassword = null;
        
        // Vérifier s'il s'agit d'une création ou d'une modification
        if(annotateur.getId() == null) {
            // Création d'un nouvel annotateur
            clearPassword = generateRandomPassword();
            annotateur.setPassword(passwordEncoder.encode(clearPassword));
            annotateur.setIsActive(true);

            Role userRole = roleRepository.findById(2L).orElseThrow(() ->
                    new RuntimeException("Le rôle USER_ROLE avec ID 2 n'a pas été trouvé"));
            annotateur.setRole(userRole);
        } else {
            // Modification d'un annotateur existant
            existingAnnotateur = annotateurRepository.findById(annotateur.getId())
                    .orElseThrow(() -> new RuntimeException("Annotateur introuvable"));

            annotateur.setPassword(existingAnnotateur.getPassword());

            annotateur.setIsActive(existingAnnotateur.getIsActive());

            annotateur.setRole(existingAnnotateur.getRole());
        }

        annotateurRepository.save(annotateur);

        if(clearPassword != null) {
            try {
                if(annotateur.getEmail() == null || annotateur.getEmail().isEmpty()) {
                    redirectAttributes.addFlashAttribute("errorMessage",
                            "L'utilisateur a été créé mais l'email est introuvable. Mot de passe: " + clearPassword);
                    redirectAttributes.addFlashAttribute("generatedPassword", clearPassword);
                    redirectAttributes.addFlashAttribute("newUser", annotateur);
                } else {
                    emailService.sendPasswordEmail(annotateur.getEmail(), annotateur.getLogin(), clearPassword);
                    redirectAttributes.addFlashAttribute("successMessage",
                            "L'utilisateur a été créé avec succès. Les identifiants ont été envoyés par email à " + annotateur.getEmail());
                }
            } catch (MessagingException e) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "L'utilisateur a été créé mais l'envoi d'email a échoué. Mot de passe: " + clearPassword);
                redirectAttributes.addFlashAttribute("generatedPassword", clearPassword);
                redirectAttributes.addFlashAttribute("newUser", annotateur);
            }
        } else {
            // Message pour la modification réussie
            redirectAttributes.addFlashAttribute("successMessage", 
                    "L'utilisateur a été modifié avec succès.");
        }

        return "redirect:/admin/user/list?page="+page+"&keyword="+keyword;
    }


    @GetMapping("user/edit")
    public String edit(Model model, Long id, String keyword, int page){
        Annotateur annotateur = annotateurRepository.findById(id).orElse(null);
        if (annotateur == null) throw new RuntimeException("Annotateur introuvable");
        model.addAttribute("annotateur", annotateur);
        model.addAttribute("keyword", keyword);
        model.addAttribute("page", page);
        return "admin/GererUser/editUser";
    }

    private String generateRandomPassword() {
        String digits = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        int length = 8;

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(digits.length());
            sb.append(digits.charAt(index));
        }

        return sb.toString();
    }
}