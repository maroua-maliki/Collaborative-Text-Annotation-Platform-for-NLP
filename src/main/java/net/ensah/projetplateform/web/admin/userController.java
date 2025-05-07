package net.ensah.projetplateform.web.admin;

import jakarta.validation.Valid;
import net.ensah.projetplateform.entities.Annotateur;
import net.ensah.projetplateform.entities.Role;
import net.ensah.projetplateform.repository.AnnotateurRepository;
import net.ensah.projetplateform.repository.RoleRepository;
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

@Controller
@RequestMapping("/admin")
public class userController {

    @Autowired
    private AnnotateurRepository annotateurRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/user/list")
    public String list(Model model,
                       @RequestParam(name = "page", defaultValue = "0") int page,
                       @RequestParam(name = "size", defaultValue = "5") int size,
                       @RequestParam(name = "keyword", defaultValue = "") String keyword) {

        Page<Annotateur> pageAnnotateur = annotateurRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(
                keyword, keyword, PageRequest.of(page, size));

        model.addAttribute("listeAnnotateur", pageAnnotateur.getContent());
        model.addAttribute("pages", new int[pageAnnotateur.getTotalPages()]);
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", keyword);

        return "admin/GererUser/listUser";
    }

    @GetMapping("/user/delete")
    public String delete(@RequestParam(name = "id") Long id , String keyword,int page ){
        annotateurRepository.deleteById(id);
        return "redirect:/admin/user/list?page="+page+"&keyword="+keyword;
    }

    @GetMapping("/annotateurs")
    @ResponseBody
    public List<Annotateur> listAnnotateur() {
        return annotateurRepository.findAll();
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

            Role userRole = roleRepository.findById(2L).orElseThrow(() ->
                    new RuntimeException("Le rôle USER_ROLE avec ID 2 n'a pas été trouvé"));
            annotateur.setRole(userRole);
        }

        annotateurRepository.save(annotateur);

        if(clearPassword != null) {
            redirectAttributes.addFlashAttribute("generatedPassword", clearPassword);
            redirectAttributes.addFlashAttribute("newUser", annotateur);
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

    // Méthode pour générer un mot de passe simple avec uniquement des chiffres
    private String generateRandomPassword() {
        String digits = "0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        int length = 4; // Par exemple, un mot de passe de 6 chiffres

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(digits.length());
            sb.append(digits.charAt(index));
        }

        return sb.toString();
    }
}