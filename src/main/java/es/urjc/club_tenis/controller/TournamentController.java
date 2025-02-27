package es.urjc.club_tenis.controller;

import es.urjc.club_tenis.model.Tournament;
import es.urjc.club_tenis.model.User;
import es.urjc.club_tenis.service.TournamentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/tournament")
public class TournamentController {

    private final TournamentService tournamentService;

    public TournamentController(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @GetMapping("/{id}")
    public String getTournament(Model model, @PathVariable long id) {

        Tournament tournament = tournamentService.findById(id);

        if (tournament == null) {
            return "redirect:/tournaments";
        }

        model.addAttribute("tournament", tournament);
        model.addAttribute("participants", tournament.getParticipants());
        model.addAttribute("matches", tournament.getMatches());

        return "tournament";
    }

    @GetMapping("/new")
    public String newTournament(Model model) {
        return "tournament_new";
    }

    @PostMapping("/new")
    public String registerUser(Model model, String name, String initDate, String endDate, int price) {

        LocalDate newInitDate = LocalDate.parse(initDate);
        LocalDate newEndDate = LocalDate.parse(endDate);

        Tournament newTournament = tournamentService.save(name, newInitDate, newEndDate, price);

        model.addAttribute("tournament", newTournament.getId());

        return "redirect:/tournaments";
    }
}
