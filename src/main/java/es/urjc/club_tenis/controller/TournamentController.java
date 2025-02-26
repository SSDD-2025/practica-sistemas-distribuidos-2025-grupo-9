package es.urjc.club_tenis.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/tournament")
public class TournamentController {

    @GetMapping("/{id}")
    public String getTournament(Model model, @PathVariable long id) {
        return "tournament";
    }
}
