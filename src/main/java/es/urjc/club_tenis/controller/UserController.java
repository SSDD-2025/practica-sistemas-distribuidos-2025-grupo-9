package es.urjc.club_tenis.controller;

import es.urjc.club_tenis.model.TennisMatch;
import es.urjc.club_tenis.model.User;
import es.urjc.club_tenis.service.MatchService;
import es.urjc.club_tenis.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.*;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

@Controller
public class UserController {

    Logger logger = Logger.getLogger("es.urjc.club_tenis.controller");

    @Autowired
    private UserService userService;

    @Autowired
    private MatchService matchService;

    @GetMapping("/profile/{username}")
    public String getProfilePage(Model model, HttpSession session, @PathVariable String username){
        User user = userService.findByUsername(username);
        model.addAttribute("profilePictureUrl" ,"/profile-picture/" + user.getUsername());
        model.addAttribute("profileUser", user);
        model.addAttribute("matches", user.getPlayedMatches());
        User sessionUser = (User) session.getAttribute("user");
        model.addAttribute("user",sessionUser);

        if(sessionUser == null){
            return "profile";
        } else if( sessionUser.equals(user) || sessionUser.isAdmin()){
            model.addAttribute("showModify", true);
            if(!user.isAdmin() && !user.equals(userService.findByUsername("deleted_user"))){
                model.addAttribute("showDelete", true);
            }
        }
        return "profile";
    }

    @GetMapping("/profile-picture/{username}")
    public ResponseEntity<byte[]> getProfilePicture(@PathVariable String username) {
        byte[] imageData = userService.getProfilePicture(username);

        if (imageData != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG) // Or determine the correct MIME type
                    .body(imageData);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/profile/{username}/modify")
    public String getModifyPage(Model model, HttpSession session, @PathVariable String username){
        User currentUser = (User) session.getAttribute("user");
        User user = userService.findByUsername(username);
        if(currentUser == null){
            return "redirect:/login";
        }
        if(currentUser.isAdmin() || currentUser.equals(user)){
            model.addAttribute("showPasswordInput", false);
            if(currentUser.equals(user)){
                model.addAttribute("showPasswordInput", true);
                model.addAttribute("showPasswordChange", true);
            }
            model.addAttribute("user", user);
            model.addAttribute("actionName", "Actualizar ");
            model.addAttribute("action", "/profile/" + username + "/modify");
            return "register";
        }else{
            model.addAttribute("errorMessage", "No tines permiso para eliminar este usuario");
            return "error";
        }
    }

    @PostMapping("/profile/{oldUsername}/modify")
    public String modifyProfilePage(Model model, HttpSession session, @PathVariable String oldUsername, String username, String name, String password, String newPassword, MultipartFile profilePicture){
        User currentUser = (User) session.getAttribute("user");
        User user = userService.findByUsername(oldUsername);
        logger.info(profilePicture.toString());
        if(currentUser == null){
            return "redirect:/login";
        }
        if(currentUser.isAdmin() || currentUser.equals(user)){
            User modify;
            try {
                if(password.isEmpty()){
                    modify = userService.modify(oldUsername, username, name, profilePicture);
                }else if(password.equals(user.getPassword())){
                    modify = userService.modify(oldUsername, username, name, password, newPassword, profilePicture);
                }else{
                    model.addAttribute("invalidPassword", true);
                    return getModifyPage(model, session, oldUsername);
                }
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
    public String registerUser(Model model, String username, String name, String password, MultipartFile profilePicture) {
        logger.info("Username: " + username);
        logger.info("Name: " + name);
        logger.info("Password: " + password);
        if(userService.findByUsername(username) != null){
            logger.info(userService.findByUsername(username).toString());
            model.addAttribute("invalid",true);
            return "register";
        }

        User newUser = userService.save(new User(username, name, password, profilePicture));

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

    @GetMapping("/users")
    public String getUserList(Model model, HttpSession session) {

        User currentUser = (User) session.getAttribute("user");
        model.addAttribute("user", currentUser);

        if(currentUser==null || !currentUser.isAdmin()){
            model.addAttribute("errorMessage", "Necesitas ser administrador para acceder a los Usuarios");
            return "error";
        }

        List<User> users = userService.findAll();
        User deleted = userService.findByUsername("deleted_user");
        users.remove(deleted);
        model.addAttribute("users",users);
    
        return "users";
    }

    @GetMapping("/users/delete/{username}")
    public String getConfirmation(Model model, @PathVariable String username, HttpSession session){

        User currentUser= (User) session.getAttribute("user");
        User deletedUser = userService.findByUsername(username);
        model.addAttribute("user", currentUser);

        if(currentUser==null || (!currentUser.isAdmin() && !currentUser.equals(deletedUser))){
            model.addAttribute("errorMessage", "No tienes permiso para borrar este usuario");
            return "error";
        }

        model.addAttribute("deletedUser", deletedUser);

        return "confirmation";
    }

    @PostMapping("/users/delete/{username}")
    public String deleteUser(Model model, @PathVariable String username, HttpSession session){

        User currentUser = (User) session.getAttribute("user");
        User deleteUser = userService.findByUsername(username);
        model.addAttribute("user", currentUser);

        if(currentUser==null || (!currentUser.isAdmin() && !currentUser.equals(deleteUser))){
            model.addAttribute("errorMessage", "No tienes permiso para borrar este usuario");
            return "error";
        }

        Set<TennisMatch> matches = deleteUser.getPlayedMatches();

        for(TennisMatch match:matches){
            matchService.deleteUser(match.getId(), deleteUser);
        }

        if(currentUser.equals(deleteUser)){

            userService.delete(username);
            session.invalidate();

            return "redirect:/";
        }

        userService.delete(username);

        return "redirect:/users";
    }

}
