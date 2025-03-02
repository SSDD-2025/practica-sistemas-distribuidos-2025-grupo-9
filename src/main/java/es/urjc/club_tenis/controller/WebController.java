package es.urjc.club_tenis.controller;

import es.urjc.club_tenis.model.*;
import es.urjc.club_tenis.service.MatchService;
import es.urjc.club_tenis.service.TournamentService;
import es.urjc.club_tenis.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class WebController {

    @Autowired
    private UserService userService;
    @Autowired
    private TournamentService tournamentService;
    @Autowired
    private MatchService matchService;

    @GetMapping("/")
    public String homePage(Model model, HttpSession session) {
        List<Tournament> tournaments = tournamentService.findAll();
        model.addAttribute("tournaments", tournaments);
        model.addAttribute("matches", matchService.findAll());
        model.addAttribute("user", session.getAttribute("user"));
        return "index";
    }

    @GetMapping("/matches")
    public String getMatches(Model model, HttpSession session){
        model.addAttribute("matches", matchService.findAll());
        model.addAttribute("user", session.getAttribute("user"));
        return "matches";
    }

    @GetMapping("/tournaments")
    public String getTournaments(Model model, HttpSession session) {
        List<Tournament> tournaments = tournamentService.findAll();
        model.addAttribute("tournaments", tournaments);
        model.addAttribute("user", session.getAttribute("user"));
        return "tournaments";
    }

    @GetMapping("/courts")
    public String getCourts(Model model, HttpSession session) {
        model.addAttribute("user", session.getAttribute("user"));
        return "courts";
    }
}
