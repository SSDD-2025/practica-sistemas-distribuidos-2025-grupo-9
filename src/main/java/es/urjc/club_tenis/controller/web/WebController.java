package es.urjc.club_tenis.controller.web;

import es.urjc.club_tenis.dto.court.CourtBasicDTO;
import es.urjc.club_tenis.dto.court.CourtDTO;
import es.urjc.club_tenis.dto.court.CourtMapper;
import es.urjc.club_tenis.dto.tournament.TournamentDTO;
import es.urjc.club_tenis.model.*;
import es.urjc.club_tenis.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
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
    @Autowired
    private CourtMapper courtMapper;

    @GetMapping("/")
    public String homePage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        List<TournamentDTO> tournamentDTOs = tournamentService.findAll();
        model.addAttribute("tournaments", tournamentDTOs);
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
        List<TournamentDTO> tournamentDTOs = tournamentService.findAll();
        User currentUser = null;
        if(userDetails != null){
            String currentUsername = userDetails.getUsername();
            currentUser = userService.findByUsername(currentUsername);
        }
        if(currentUser != null && currentUser.isAdmin()){
            model.addAttribute("showNew", true);
            model.addAttribute("showModify", true);
        }
        model.addAttribute("tournaments", tournamentDTOs);
        model.addAttribute("user", currentUser);
        return "tournaments";
    }

    @GetMapping("/courts")
    public String getCourts(Model model, @AuthenticationPrincipal UserDetails userDetails, @RequestParam(defaultValue = "1") int page) {
        User currentUser = null;
        if(userDetails != null){
            String currentUsername = userDetails.getUsername();
            currentUser = userService.findByUsername(currentUsername);
        }
        Page<CourtDTO> courts = courtService.findAll(page);
        model.addAttribute("courts", courts);
        model.addAttribute("user", currentUser);
        if(currentUser != null && currentUser.isAdmin()){
            model.addAttribute("showModify", true);
        }

        long nCourts = courts.getTotalElements();
        if (nCourts > 5) {
            long nPages = nCourts % Court.PAGE_SIZE == 0 ? nCourts / Court.PAGE_SIZE : (nCourts / Court.PAGE_SIZE) + 1;
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

        return "courts";
    }

    @GetMapping("/error/accessDenied")
    public String accessDenied(Model model) {
        model.addAttribute("errorMessage", "Access denied, insufficient permissions");
        return "error";
    }
}
