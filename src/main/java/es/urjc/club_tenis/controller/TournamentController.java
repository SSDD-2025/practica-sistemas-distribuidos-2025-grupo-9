package es.urjc.club_tenis.controller;

import es.urjc.club_tenis.model.*;
import es.urjc.club_tenis.service.TournamentService;
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

    private final TournamentService tournamentService;

    public TournamentController(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @GetMapping("/{id}")
    public String getTournament(Model model, @PathVariable long id) {
        logger.info("Searching for tournament");
        Tournament tournament = tournamentService.findById(id);

        logger.info("Torneo: " + tournament);
        model.addAttribute("tournament", tournament);

        return "tournament";
    }

    @GetMapping("/new")
    public String newTournament(Model model) {
        return "tournament_new";
    }

    @PostMapping("/new")
    public String saveTorunament(Model model, String name, String initDate, String endDate, int price) {

        LocalDate newInitDate = LocalDate.parse(initDate);
        LocalDate newEndDate = LocalDate.parse(endDate);

        Tournament newTournament = tournamentService.save(name, newInitDate, newEndDate, price);

        model.addAttribute("tournament", newTournament.getId());

        return "redirect:/tournaments";
    }
}
