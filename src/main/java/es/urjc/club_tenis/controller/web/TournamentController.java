package es.urjc.club_tenis.controller.web;

import es.urjc.club_tenis.dto.court.CourtMapper;
import es.urjc.club_tenis.dto.match.MatchDTO;
import es.urjc.club_tenis.dto.tournament.TournamentDTO;
import es.urjc.club_tenis.dto.user.UserMapper;
import es.urjc.club_tenis.model.*;
import es.urjc.club_tenis.service.CourtService;
import es.urjc.club_tenis.service.MatchService;
import es.urjc.club_tenis.service.TournamentService;
import es.urjc.club_tenis.service.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;
@Controller
@RequestMapping("/tournament")
public class TournamentController {

    Logger logger = Logger.getLogger("es.urjc.club_tenis.controller");

    @Autowired private TournamentService tournamentService;
    @Autowired private CourtService courtService;
    @Autowired private MatchService matchService;
    @Autowired private UserService userService;

    // TEMP (hasta que migréis completamente a DTOs para User/Court)
    @Autowired private UserMapper userMapper;
    @Autowired private CourtMapper courtMapper;

    @PersistenceContext private EntityManager entityManager;

    @GetMapping("/{id}")
    public String getTournament(Model model, @PathVariable long id, @AuthenticationPrincipal UserDetails userDetails) {
        var tournament = tournamentService.findById(id);
        User currentUser = (userDetails != null) ? userService.findByUsername(userDetails.getUsername()) : null;

        if (currentUser != null && currentUser.isAdmin()) {
            model.addAttribute("showAdd", true);
        }

        logger.info("Torneo: " + tournament);
        model.addAttribute("tournament", tournament);
        model.addAttribute("user", currentUser);
        return "tournament";
    }

    @GetMapping("/{id}/addMatch")
    public String addMatch(Model model, @PathVariable long id, @AuthenticationPrincipal UserDetails userDetails) {
        var tournament = tournamentService.findById(id);
        User currentUser = (userDetails != null) ? userService.findByUsername(userDetails.getUsername()) : null;

        if (currentUser == null || !currentUser.isAdmin()) {
            model.addAttribute("errorMessage", "No se puede editar un torneo sin ser administrador");
            return "error";
        }

        model.addAttribute("actionName", "Crear ");
        model.addAttribute("action", "addMatch");
        model.addAttribute("courts", courtMapper.toBasicDTOs(courtService.findAll()));
        model.addAttribute("tournament", tournament);
        model.addAttribute("user", currentUser);
        return "tournament_addMatch";
    }

    @PostMapping("/{id}/addMatch")
    public String saveMatch(@PathVariable long id, String localUsername, String visitorUsername, long court,
                            String winnerUsername, String result, Model model,
                            @AuthenticationPrincipal UserDetails userDetails) {

        logger.info("Datos recibidos:");
        logger.info("Local: " + localUsername);
        logger.info("Visitante: " + visitorUsername);
        logger.info("Court: " + court);
        logger.info("Ganador: " + winnerUsername);
        logger.info("Resultado: " + result);

        var courtObj = courtService.findById(court);
        var local = userService.findByUsername(localUsername);
        var visitor = userService.findByUsername(visitorUsername);
        var winner = winnerUsername != null && !winnerUsername.isEmpty() ? userService.findByUsername(winnerUsername) : null;

        boolean invalid = false;
        if (local == null) { model.addAttribute("invalidLocalUsername", true); invalid = true; }
        if (visitor == null) { model.addAttribute("invalidVisitorUsername", true); invalid = true; }
        if (winnerUsername != null && !winnerUsername.isEmpty() && winner == null) {
            model.addAttribute("invalidWinnerUsername", true); invalid = true;
        }

        if (invalid) {
            model.addAttribute("action", "addMatch");
            model.addAttribute("actionName", "Crear ");
            model.addAttribute("courts", courtMapper.toBasicDTOs(courtService.findAll()));
            return "tournament_addMatch";
        }

        var currentUser = (userDetails != null) ? userService.findByUsername(userDetails.getUsername()) : null;

        var newMatch = new MatchDTO(
                null,
                userMapper.toBasicDTO(currentUser),
                (winner != null ? userMapper.toBasicDTO(winner) : null),
                userMapper.toBasicDTO(local),
                userMapper.toBasicDTO(visitor),
                courtMapper.toBasicDTO(courtObj),
                result
        );

        var savedMatch = matchService.save(newMatch);
        logger.info(savedMatch.toString());
        tournamentService.addMatch(id, savedMatch);

        return "redirect:/tournament/" + id;
    }

    @GetMapping("/new")
    public String newTournament(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = (userDetails != null) ? userService.findByUsername(userDetails.getUsername()) : null;

        if (currentUser == null || !currentUser.isAdmin()) {
            model.addAttribute("errorMessage", "No se puede crear un torneo sin ser administrador");
            model.addAttribute("user", currentUser);
            return "error";
        }

        model.addAttribute("action", "new");
        model.addAttribute("actionName", "Crear ");
        model.addAttribute("user", currentUser);
        return "tournament_new";
    }

    @PostMapping("/new")
    public String saveTorunament(Model model, String name, String initDate, String endDate, int price) {
        var dto = new TournamentDTO(null, name, LocalDate.parse(initDate), LocalDate.parse(endDate), price, List.of(), List.of());
        var newTournament = tournamentService.save(dto);
        return "redirect:/tournament/" + newTournament.id();
    }

    @GetMapping("/{id}/modify")
    public String getModifyForm(Model model, @AuthenticationPrincipal UserDetails userDetails, @PathVariable long id) {
        User currentUser = (userDetails != null) ? userService.findByUsername(userDetails.getUsername()) : null;

        if (currentUser == null || !currentUser.isAdmin()) {
            model.addAttribute("errorMessage", "No se puede editar un torneo sin ser administrador");
            return "error";
        }

        var tournament = tournamentService.findById(id);
        model.addAttribute("tournament", tournament);
        model.addAttribute("actionName", "Modificar ");
        model.addAttribute("action", id + "/modify");
        return "tournament_new";
    }

    @PostMapping("/{id}/modify")
    public String modifyTournament(Model model, @AuthenticationPrincipal UserDetails userDetails, @PathVariable long id,
                                   String name, String initDate, String endDate, int price) {

        User currentUser = (userDetails != null) ? userService.findByUsername(userDetails.getUsername()) : null;

        if (currentUser == null || !currentUser.isAdmin()) {
            model.addAttribute("errorMessage", "No se ha podido modificar el torneo");
            return "error";
        }

        var dto = new TournamentDTO(id, name, LocalDate.parse(initDate), LocalDate.parse(endDate), price, List.of(), List.of());
        tournamentService.modify(id, dto);

        return "redirect:/tournaments";
    }

    @GetMapping("/{id}/delete")
    public String deleteTournament(Model model, @AuthenticationPrincipal UserDetails userDetails, @PathVariable long id) {
        User currentUser = (userDetails != null) ? userService.findByUsername(userDetails.getUsername()) : null;

        if (currentUser == null || !currentUser.isAdmin()) {
            model.addAttribute("errorMessage", "No se ha podido eliminar el torneo");
            return "error";
        }

        tournamentService.delete(id);
        return "redirect:/tournaments";
    }
}

