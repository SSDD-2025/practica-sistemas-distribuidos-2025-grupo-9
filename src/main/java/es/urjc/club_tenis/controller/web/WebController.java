package es.urjc.club_tenis.controller.web;

import es.urjc.club_tenis.model.*;
import es.urjc.club_tenis.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    public String homePage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        List<Tournament> tournaments = tournamentService.findAll();
        model.addAttribute("tournaments", tournaments);
        model.addAttribute("matches", matchService.findAll());
        if(userDetails!=null){
            String currentUsername = userDetails.getUsername();
            User currentUser = userService.findByUsername(currentUsername);
            model.addAttribute("user", currentUser);
        }else{
            model.addAttribute("user", null);
        }

        return "index";
    }

    @GetMapping("/error")
    public String errorPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        return "error";
    }

    @GetMapping("/matches")
    public String getMatches(Model model, @AuthenticationPrincipal UserDetails userDetails){
        model.addAttribute("matches", matchService.findAll());
        if(userDetails!=null){
            String currentUsername = userDetails.getUsername();
            User currentUser = userService.findByUsername(currentUsername);
            model.addAttribute("user", currentUser);
        }else{
            model.addAttribute("user", null);
        }

        return "matches";
    }

    @GetMapping("/tournaments")
    public String getTournaments(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        List<Tournament> tournaments = tournamentService.findAll();
        User currentUser = null;
        if(userDetails != null){
            String currentUsername = userDetails.getUsername();
            currentUser = userService.findByUsername(currentUsername);
        }
        if(currentUser != null && currentUser.isAdmin()){
            model.addAttribute("showNew", true);
            model.addAttribute("showModify", true);
        }
        model.addAttribute("tournaments", tournaments);
        model.addAttribute("user", currentUser);
        return "tournaments";
    }

    @GetMapping("/courts")
    public String getCourts(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = null;
        if(userDetails != null){
            String currentUsername = userDetails.getUsername();
            currentUser = userService.findByUsername(currentUsername);
        }
        List<Court> courts = courtService.findAll();
        model.addAttribute("courts",courts);
        model.addAttribute("user", currentUser);
        if(currentUser != null && currentUser.isAdmin()){
            model.addAttribute("showModify", true);
        }
        return "courts";
    }

    @GetMapping("/error/accessDenied")
    public String accessDenied(Model model) {
        model.addAttribute("errorMessage", "Access denied, insufficient permissions");
        return "error";
    }
}
