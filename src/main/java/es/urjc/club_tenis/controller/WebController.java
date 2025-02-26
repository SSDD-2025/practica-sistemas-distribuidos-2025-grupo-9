package es.urjc.club_tenis.controller;

import es.urjc.club_tenis.model.*;
import es.urjc.club_tenis.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

import java.util.List;

@Controller
public class WebController {

    @Autowired
    private UserService userService;

    @GetMapping("/match")
    public String getMatchPage(Model model){
        model.addAttribute("owner", true);
        return "match";
    }

    @GetMapping("/")
    public String homePage(Model model) {
        List<User> users = userService.findAll();  // Obtén todos los usuarios
        model.addAttribute("users", users);    // Pasa la lista de usuarios al modelo
        return "index";  // O el nombre de tu plantilla principal
    }

    //para resesrvar pista se puede hacer con un mapa con el tiempo como clave y con un usuario como booleano para reserva
}
