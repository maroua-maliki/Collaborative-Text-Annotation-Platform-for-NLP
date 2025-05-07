package net.ensah.projetplateform.web;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class loginController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }


    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }

    @GetMapping("/admin/home")
    public String showAdminHome(Model model) {
        return "admin/home";
    }

    @GetMapping("/user/home")
    public String showUserHome(Model model) {
        return "user/home";
    }

}
