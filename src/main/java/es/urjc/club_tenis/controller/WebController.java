package es.urjc.club_tenis.controller;

import es.urjc.club_tenis.model.*;
import es.urjc.club_tenis.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

@Controller
public class WebController {

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

    @GetMapping("/match")
    public String getMatchPage(Model model){
        model.addAttribute("owner", true);
        return "match";
    }

    //para resesrvar pista se puede hacer con un mapa con el tiempo como clave y con un usuario como booleano para reserva
}
