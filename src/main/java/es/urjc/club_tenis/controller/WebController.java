package es.urjc.club_tenis.controller;

import es.urjc.club_tenis.model.*;
import es.urjc.club_tenis.service.TournamentService;
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
    @Autowired
    private TournamentService tournamentService;

    @GetMapping("/match")
    public String getMatchPage(Model model){
        model.addAttribute("owner", true);
        return "match";
    }

    @GetMapping("/")
    public String homePage(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        List<Tournament> tournaments = tournamentService.findAll();
        model.addAttribute("tournaments", tournaments);
        return "index";
    }

    @GetMapping("/tournaments")
    public String getTournaments(Model model) {
        List<Tournament> tournaments = tournamentService.findAll();
        model.addAttribute("tournaments", tournaments);
        return "tournaments";
    }

    @GetMapping("/courts")
    public String getCourts(Model model) {
        return "courts";
    }

    @GetMapping("/matches")
    public String getMatches(Model model) {
        return "matches";
    }

    //para resesrvar pista se puede hacer con un mapa con el tiempo como clave y con un usuario como booleano para reserva
}
