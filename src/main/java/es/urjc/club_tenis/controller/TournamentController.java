package es.urjc.club_tenis.controller;

import es.urjc.club_tenis.model.*;
import es.urjc.club_tenis.service.CourtService;
import es.urjc.club_tenis.service.MatchService;
import es.urjc.club_tenis.service.TournamentService;
import es.urjc.club_tenis.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

@Controller
@RequestMapping("/tournament")
public class TournamentController {

    Logger logger = Logger.getLogger("es.urjc.club_tenis.controller");

    @Autowired
    private TournamentService tournamentService;
    @Autowired
    private CourtService courtService;
    @Autowired
    private MatchService matchService;
    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public String getTournament(Model model, @PathVariable long id, HttpSession session) {
        logger.info("Searching for tournament");
        Tournament tournament = tournamentService.findById(id);
        User currentUser = (User) session.getAttribute("user");
        if(currentUser != null && currentUser.isAdmin()){
            model.addAttribute("showAdd", true);
        }
        logger.info("Torneo: " + tournament);
        model.addAttribute("tournament", tournament);
        model.addAttribute("user", session.getAttribute("user"));
        return "tournament";
    }

    @GetMapping("/{id}/addMatch")
    public String addMatch(Model model, @PathVariable long id, HttpSession session) {
        logger.info("Searching for tournament");
        Tournament tournament = tournamentService.findById(id);
        User currentUser = (User) session.getAttribute("user");
        if(currentUser == null || !currentUser.isAdmin()){
            model.addAttribute("errorMessage", "No se puede editar un torneo sin ser administrador");
            return "error";
        }
        logger.info("Torneo: " + tournament);
        model.addAttribute("actionName", "Crear ");
        model.addAttribute("action", "addMatch");
        model.addAttribute("courts", courtService.findAll());
        model.addAttribute("tournament", tournament);
        model.addAttribute("user", session.getAttribute("user"));
        return "tournament_addMatch";
    }

    @PostMapping("/{id}/addMatch")
    public String saveMatch(@PathVariable long id, String localUsername, String visitorUsername, long court, String winnerUsername, String result, Model model, HttpSession session){

        System.out.println("Datos recibidos:");
        System.out.println("Local: " + localUsername);
        System.out.println("Visitante: " + visitorUsername);
        System.out.println("Court: " + court);
        System.out.println("Ganador: " + winnerUsername);
        System.out.println("Resultado: " + result);

        Court courtObj = courtService.findById(court);
        User local = userService.findByUsername(localUsername);
        User visitor = userService.findByUsername(visitorUsername);
        User winner = userService.findByUsername(winnerUsername);

        /*Tournament actualTournament = tournamentService.findById(tournament_id);*/

        boolean invalid = false;
        if(local == null){
            model.addAttribute("invalidLocalUsername", true);
            invalid = true;
        }
        if(visitor == null){
            model.addAttribute("invalidVisitorUsername", true);
            invalid = true;
        }
        if(winner == null && !winnerUsername.isEmpty()){
            model.addAttribute("invalidWinnerUsername", true);
            invalid = true;
        }
        //Court can't be null unless they change html from inspection

        if(invalid){
            model.addAttribute("action", "addMatch");
            model.addAttribute("actionName", "Crear ");
            model.addAttribute("courts", courtService.findAll());
            return "tournament_addMatch";
        }else {
            User currentUser = (User) session.getAttribute("user");
            TennisMatch newMatch = matchService.createMatch(currentUser,local, visitor, courtObj, winner, result);
            //newMatch.setTournament(actualTournament);
            //actualTournament.addMatch(newMatch);
            return "redirect:/tournament/" + id;
        }
    }

    @GetMapping("/new")
    public String newTournament(Model model, HttpSession session) {
        model.addAttribute("user", session.getAttribute("user"));
        User currentUser = (User) session.getAttribute("user");
        if(currentUser == null){
            model.addAttribute("errorMessage", "No se puede crear un torneo sin ser administrador");
            return "error";
        }
        if(!currentUser.isAdmin()){
            model.addAttribute("errorMessage", "Un usuario sin privilegios no puede crear un torneo");
            model.addAttribute("user", currentUser);
            return "error";
        }
        return "tournament_new";
    }

    @PostMapping("/new")
    public String saveTorunament(Model model, String name, String initDate, String endDate, int price) {

        LocalDate newInitDate = LocalDate.parse(initDate);
        LocalDate newEndDate = LocalDate.parse(endDate);

        Tournament newTournament = tournamentService.save(new Tournament(name, newInitDate, newEndDate, price));

        model.addAttribute("tournament", newTournament.getId());

        return "redirect:/tournaments";
    }
}
