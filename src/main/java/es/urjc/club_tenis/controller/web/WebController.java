package es.urjc.club_tenis.controller.web;

import es.urjc.club_tenis.model.*;
import es.urjc.club_tenis.service.*;
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
    @Autowired
    private CourtService courtService;

    @GetMapping("/")
    public String homePage(Model model, HttpSession session) {
        List<Tournament> tournaments = tournamentService.findAll();
        model.addAttribute("tournaments", tournaments);
        model.addAttribute("matches", matchService.findAll());
        model.addAttribute("user", session.getAttribute("user"));
        return "index";
    }

    @GetMapping("/error")
    public String errorPage(Model model, HttpSession session) {
        return "error";
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
        User currentUser = (User) session.getAttribute("user");
        if(currentUser != null && currentUser.isAdmin()){
            model.addAttribute("showNew", true);
            model.addAttribute("showModify", true);
        }
        model.addAttribute("tournaments", tournaments);
        model.addAttribute("user", session.getAttribute("user"));
        return "tournaments";
    }

    @GetMapping("/courts")
    public String getCourts(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        List<Court> courts = courtService.findAll();
        model.addAttribute("courts",courts);
        model.addAttribute("user", currentUser);
        if(currentUser != null && currentUser.isAdmin()){
            model.addAttribute("showModify", true);
        }
        return "courts";
    }
}
