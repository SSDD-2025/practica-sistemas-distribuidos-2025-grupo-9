package es.urjc.club_tenis.controller;

import es.urjc.club_tenis.model.User;
import es.urjc.club_tenis.service.UserService;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@Controller
public class UserController {

    @Autowired
    private UserService service;

    @GetMapping("/users/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }


    @PostMapping("/users/register")
    public String registerUser(Model model, String username, String name, String password) {

        User newUser = service.save(username, name, password);

        model.addAttribute("user", newUser.getId());

        return "redirect:/profile/" + newUser.getUsername();
    }

}
