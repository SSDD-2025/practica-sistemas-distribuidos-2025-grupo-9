package es.urjc.club_tenis.controller;

import es.urjc.club_tenis.model.User;
import es.urjc.club_tenis.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.*;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
        User currentUser = (User) session.getAttribute("user");
        User user = userService.findByUsername(username);
        if(currentUser == null){
            return "redirect:/login";
        }
        if(currentUser.isAdmin() || currentUser.equals(user)){
            model.addAttribute("user", user);
            model.addAttribute("showPasswordInput", false);
            model.addAttribute("actionName", "Actualizar ");
            model.addAttribute("action", "/profile/" + username + "/modify");
            return "register";
        }else{
            model.addAttribute("errorMessage", "No tines permiso para eliminar este usuario");
            return "error";
        }
    }

    @PostMapping("/profile/{oldUsername}/modify")
    public String modifyProfilePage(Model model, HttpSession session, @PathVariable String oldUsername, String username, String name){
        User currentUser = (User) session.getAttribute("user");
        User user = userService.findByUsername(oldUsername);
        if(currentUser == null){
            return "redirect:/login";
        }
        if(currentUser.isAdmin() || currentUser.equals(user)){

            try {
                User modify = userService.modify(user, username, name);
                logger.info(modify.toString());
                return "redirect:/profile/" + modify.getUsername();
            } catch (ChangeSetPersister.NotFoundException e) {
                model.addAttribute("errorMessage", "No se ha encontrado este usuario");
                return "error";
            }
        }else{
            model.addAttribute("errorMessage", "No tines permiso para eliminar este usuario");
            return "error";
        }
    }

    @GetMapping("/signin")
    public String showRegistrationForm(Model model) {
        model.addAttribute("actionName", "Registrar ");
        model.addAttribute("action", "/signin");
        model.addAttribute("showPasswordInput", true);
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
