package es.urjc.club_tenis.controller;

import es.urjc.club_tenis.model.User;
import es.urjc.club_tenis.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile/{username}")
    public String getProfilePage(Model model, @PathVariable String username){
        User user = userService.findByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("followingOthers", user.followedUsers.size());
        model.addAttribute("followingMe", user.followedBy.size());
        return "profile";
    }

    @GetMapping("/users/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }


    @PostMapping("/users/register")
    public String registerUser(Model model, String username, String name, String password) {

        User newUser = userService.save(username, name, password);

        model.addAttribute("user", newUser.getId());

        return "redirect:/profile/" + newUser.getUsername();
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    // Procesar el login
    @PostMapping("/login")
    public String loginUser(@RequestParam String username, @RequestParam String password, HttpSession session, Model model) {

        User user = userService.findByUsername(username);

        if (user != null && user.getPassword().equals(password)) {

            session.setAttribute("user", user);
            return "redirect:/";
        } else {
            model.addAttribute("error", "Invalid credentials");
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
