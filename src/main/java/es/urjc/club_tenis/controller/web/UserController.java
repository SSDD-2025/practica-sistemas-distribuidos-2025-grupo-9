package es.urjc.club_tenis.controller.web;

import es.urjc.club_tenis.dto.user.UserBasicDTO;
import es.urjc.club_tenis.dto.user.UserDTO;
import es.urjc.club_tenis.dto.user.UserMapper;
import es.urjc.club_tenis.model.Court;
import es.urjc.club_tenis.model.TennisMatch;
import es.urjc.club_tenis.model.Tournament;
import es.urjc.club_tenis.model.User;
import es.urjc.club_tenis.service.MatchService;
import es.urjc.club_tenis.service.UserService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.*;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
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

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/profile/{username}")
    public String getProfilePage(Model model, @AuthenticationPrincipal UserDetails userDetails,
                                 @PathVariable String username){
        UserDTO user = userMapper.toDTO(userService.findByUsername(username));
        model.addAttribute("profilePictureUrl" ,"/profile-picture/" + user.username());
        model.addAttribute("profileUser", user);
        model.addAttribute("matches", user.playedMatches());
        UserDTO currentUser = null;
        String currentUsername = null;
        if(userDetails != null){
            currentUsername = userDetails.getUsername();
            currentUser = userMapper.toDTO(userService.findByUsername(currentUsername));
        }
        model.addAttribute("user",currentUser);

        if(currentUser == null){
            return "profile";
        } else if( currentUser.equals(user) || userService.findByUsername(currentUsername).isAdmin()){
            model.addAttribute("showModify", true);
            if(!userService.findByUsername(user.username()).isAdmin() && !user.username().equals("deleted_user")){
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
    public String getModifyPage(Model model, @AuthenticationPrincipal UserDetails userDetails, @PathVariable String username){
        UserDTO user = userMapper.toDTO(userService.findByUsername(username));
        String currentUsername = userDetails.getUsername();
        UserDTO currentUser = userMapper.toDTO(userService.findByUsername(currentUsername));
        if(currentUser == null){
            return "redirect:/login";
        }
        if(userService.findByUsername(currentUsername).isAdmin() || currentUser.equals(user)){
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
    public String modifyProfilePage(Model model, @AuthenticationPrincipal UserDetails userDetails, @PathVariable String oldUsername, String username, String name, String password, String newPassword, MultipartFile profilePicture){
        String currentUsername = userDetails.getUsername();
        UserDTO currentUser = userMapper.toDTO(userService.findByUsername(currentUsername));
        UserDTO user = userMapper.toDTO(userService.findByUsername(oldUsername));
        logger.info(profilePicture.toString());
        if(currentUser == null){
            return "redirect:/login";
        }
        if(userService.findByUsername(currentUsername).isAdmin() || currentUser.equals(user)){
            User modify;
            String encodedPassword = passwordEncoder.encode(password);
            String newEncodedPassword = passwordEncoder.encode(newPassword);
            try {
                if(password.isEmpty()){
                    modify = userService.modify(oldUsername, username, name, profilePicture);
                }else if(encodedPassword.equals(userService.findByUsername(oldUsername).getPassword())){
                    modify = userService.modify(oldUsername, username, name, encodedPassword, newEncodedPassword, profilePicture);
                }else{
                    model.addAttribute("invalidPassword", true);
                    return getModifyPage(model, userDetails, oldUsername);
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
        String encodedPassword = passwordEncoder.encode(password);
        User newUser = userService.save(new User(username, name, encodedPassword, profilePicture));
        newUser.addRole("USER");

        model.addAttribute("user", newUser.getId());

        return "redirect:/profile/" + newUser.getUsername();
    }

    @GetMapping("/login")
    public String showLoginPage(@RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "expired", required = false) String expired, Model model) {
        if (error != null) {
            model.addAttribute("error", true);
        }
        if (expired != null) {
            model.addAttribute("error", "Sesión expirada");
        }

        return "login";
    }

    @GetMapping("/users")
    public String getUserList(Model model, @AuthenticationPrincipal UserDetails userDetails, @RequestParam(defaultValue = "1") int page) {

        UserDTO currentUser = null;
        String currentUsername = null;
        if(userDetails != null){
            currentUsername = userDetails.getUsername();
            currentUser = userMapper.toDTO(userService.findByUsername(currentUsername));
        }
        model.addAttribute("user", currentUser);

        if(currentUser==null || !userService.findByUsername(currentUsername).isAdmin()){
            model.addAttribute("errorMessage", "Necesitas ser administrador para acceder a los Usuarios");
            return "error";
        }


        Page<User> users = userService.findAll(page);
        List<User> usersList = new ArrayList<>();
        for (User user : users){
            if(!user.getRoles().contains("ADMIN") || !user.getUsername().equals("deleted_user")){
                usersList.add(user);
            }
        }

        model.addAttribute("users",usersList);
        long nUsers = users.getTotalElements();
        if (nUsers > 5) {
            long nPages = nUsers % User.PAGE_SIZE == 0 ? nUsers / User.PAGE_SIZE : (nUsers / User.PAGE_SIZE) + 1;
            ArrayList<Integer> pages = new ArrayList<>();
            for (int i = 1; i <= nPages; i++) {
                pages.add(i);
            }
            model.addAttribute("pages", pages);
            if (nPages > page)
                model.addAttribute("nextPage", page + 1);
            if (page > 1)
                model.addAttribute("previousPage", page - 1);
        }

        return "users";
    }

    @GetMapping("/users/delete/{username}")
    public String getConfirmation(Model model, @PathVariable String username, @AuthenticationPrincipal UserDetails userDetails){

        String currentUsername = userDetails.getUsername();
        UserDTO currentUser = userMapper.toDTO(userService.findByUsername(currentUsername));
        UserDTO deletedUser = userMapper.toDTO(userService.findByUsername(username));
        model.addAttribute("user", currentUser);

        if(currentUser==null || (!userService.findByUsername(currentUsername).isAdmin() && !currentUser.equals(deletedUser))){
            model.addAttribute("errorMessage", "No tienes permiso para borrar este usuario");
            return "error";
        }

        model.addAttribute("deletedUser", deletedUser);

        return "confirmation";
    }

    @PostMapping("/users/delete/{username}")
    public String deleteUser(Model model, @PathVariable String username,
                             @AuthenticationPrincipal UserDetails userDetails,
                             HttpServletRequest request){
        logger.info("Deleting User :" + username);
        String currentUsername = userDetails.getUsername();
        User currentUser = userService.findByUsername(currentUsername);
        User deleteUser = userService.findByUsername(username);
        model.addAttribute("user", currentUser);

        if (currentUser == null || (!userService.findByUsername(currentUsername).isAdmin() && !currentUser.equals(deleteUser))) {
            model.addAttribute("errorMessage", "No tienes permiso para borrar este usuario");
            return "error";
        }

        Set<TennisMatch> matches = userService.findByUsername(username).getPlayedMatches();

        logger.warning("MEEEEEEEEEEEEEEEEEEEEEEEEE: MatchListSIZE: " + matches.size());
        for (TennisMatch match : matches) {
            matchService.deleteUser(match.getId(), deleteUser);
            //userService.removeMatch(username, match);
        }

        if (currentUser.equals(deleteUser)) {
            request.getSession().invalidate();
        }

        userService.delete(username);

        return currentUser.equals(deleteUser) ? "redirect:/login" : "redirect:/users";
    }

}
