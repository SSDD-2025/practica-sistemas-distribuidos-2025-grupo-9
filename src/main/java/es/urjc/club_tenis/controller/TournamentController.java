package es.urjc.club_tenis.controller;

import es.urjc.club_tenis.model.*;
import es.urjc.club_tenis.service.TournamentService;
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

    @GetMapping("/{id}")
    public String getTournament(Model model, @PathVariable long id, HttpSession session) {
        logger.info("Searching for tournament");
        Tournament tournament = tournamentService.findById(id);

        logger.info("Torneo: " + tournament);
        model.addAttribute("tournament", tournament);
        model.addAttribute("user", session.getAttribute("user"));
        return "tournament";
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
