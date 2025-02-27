package es.urjc.club_tenis.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tournament")
public class TournamentController {

    @GetMapping("/{id}")
    public String getTournament(Model model, @PathVariable long id) {
        return "tournament";
    }

    @GetMapping("/new")
    public String newTournament(Model model) {
        return "tournament_new";
    }
}
