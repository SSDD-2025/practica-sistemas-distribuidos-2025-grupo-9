package es.urjc.club_tenis.controller;

import es.urjc.club_tenis.model.User;
import es.urjc.club_tenis.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.logging.Logger;

@Controller
public class UserController {

    Logger logger = Logger.getLogger("es.urjc.club_tenis.controller");

    @Autowired
    private UserService userService;

    @GetMapping("/profile/{username}")
    public String getProfilePage(Model model, HttpSession session, @PathVariable String username){
        User user = userService.findByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("matches", user.getPlayedMatches());
        User sessionUser = (User) session.getAttribute("user");
        if(sessionUser == null){
            return "profile";
        } else if( sessionUser.equals(user) || sessionUser.isAdmin()){
            model.addAttribute("showModify", true);
        }
        return "profile";
    }

    @GetMapping("/profile/{username}/modify")
    public String getModifyPage(Model model, HttpSession session, @PathVariable String username){
        User user = userService.findByUsername(username);
        model.addAttribute("user", user);
        return "modify_profile";
    }

    @GetMapping("/signin")
    public String showRegistrationForm(Model model) {
        return "register";
    }


    @PostMapping("/signin")
    public String registerUser(Model model, String username, String name, String password) {
        logger.info("Username: " + username);
        logger.info("Name: " + name);
        logger.info("Password: " + password);
        if(userService.findByUsername(username) != null){
            logger.info(userService.findByUsername(username).toString());
            model.addAttribute("invalid",true);
            return "register";
        }

        User newUser = userService.save(new User(username, name, password));

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
            model.addAttribute("invalid", true);
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
