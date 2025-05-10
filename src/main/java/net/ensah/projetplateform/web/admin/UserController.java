package net.ensah.projetplateform.web.admin;


import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import net.ensah.projetplateform.entities.Annotateur;
import net.ensah.projetplateform.entities.Role;
import net.ensah.projetplateform.repository.AnnotateurRepository;
import net.ensah.projetplateform.repository.RoleRepository;
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
public class userController {

    @Autowired
    private AnnotateurRepository annotateurRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;


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
        Annotateur annotateur = annotateurRepository.findById(id).orElse(null);
        if (annotateur != null) {
            annotateur.setIsActive(false);
            annotateurRepository.saveAndFlush(annotateur);
        }
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

        if(bindingResult.hasErrors()) return "admin/GererUser/formUser";

        String clearPassword = null;
        if(annotateur.getId() == null) {
            clearPassword = generateRandomPassword();
            annotateur.setPassword(passwordEncoder.encode(clearPassword));
            annotateur.setIsActive(true);

            Role userRole = roleRepository.findById(2L).orElseThrow(() ->
                    new RuntimeException("Le rôle USER_ROLE avec ID 2 n'a pas été trouvé"));
            annotateur.setRole(userRole);
        }

        annotateurRepository.save(annotateur);

        if(clearPassword != null) {
            try {
                emailService.sendPasswordEmail(annotateur.getEmail(), annotateur.getLogin(), clearPassword);
                redirectAttributes.addFlashAttribute("successMessage",
                        "L'utilisateur a été créé avec succès. Les identifiants ont été envoyés par email à " + annotateur.getEmail());
            } catch (MessagingException e) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "L'utilisateur a été créé mais l'envoi d'email a échoué. Mot de passe: " + clearPassword);
            }
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
        String digits = "0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        int length = 4;

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(digits.length());
            sb.append(digits.charAt(index));
        }

        return sb.toString();
    }
}