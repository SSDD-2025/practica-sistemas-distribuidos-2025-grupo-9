package es.urjc.club_tenis.controller.web;

import es.urjc.club_tenis.dto.court.CourtMapper;
import es.urjc.club_tenis.dto.match.MatchDTO;
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

    //Temporal hasta que se use UserDTO y CourtDTO aquí
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CourtMapper courtMapper;

    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping("/{id}")
    public String getTournament(Model model, @PathVariable long id, @AuthenticationPrincipal UserDetails userDetails) {
        logger.info("Searching for tournament");
        Tournament tournament = tournamentService.findById(id);
        User currentUser = null;
        if(userDetails != null){
            String currentUsername = userDetails.getUsername();
            currentUser = userService.findByUsername(currentUsername);
        }
        if(currentUser != null && currentUser.isAdmin()){
            model.addAttribute("showAdd", true);
        }
        logger.info("Torneo: " + tournament);
        model.addAttribute("tournament", tournament);
        model.addAttribute("user", currentUser);
        return "tournament";
    }

    @GetMapping("/{id}/addMatch")
    public String addMatch(Model model, @PathVariable long id, @AuthenticationPrincipal UserDetails userDetails) {
        logger.info("Searching for tournament");
        Tournament tournament = tournamentService.findById(id);
        User currentUser = null;
        if(userDetails != null){
            String currentUsername = userDetails.getUsername();
            currentUser = userService.findByUsername(currentUsername);
        }
        if(currentUser == null || !currentUser.isAdmin()){
            model.addAttribute("errorMessage", "No se puede editar un torneo sin ser administrador");
            return "error";
        }
        logger.info("Torneo: " + tournament);
        model.addAttribute("actionName", "Crear ");
        model.addAttribute("action", "addMatch");
        model.addAttribute("courts", courtMapper.toBasicDTOs(courtService.findAll()));
        model.addAttribute("tournament", tournament);
        model.addAttribute("user", currentUser);
        return "tournament_addMatch";
    }

    @PostMapping("/{id}/addMatch")
    public String saveMatch(@PathVariable long id, String localUsername, String visitorUsername, long court, String winnerUsername, String result, Model model, @AuthenticationPrincipal UserDetails userDetails){

        logger.info("Datos recibidos:");
        logger.info("Local: " + localUsername);
        logger.info("Visitante: " + visitorUsername);
        logger.info("Court: " + court);
        logger.info("Ganador: " + winnerUsername);
        logger.info("Resultado: " + result);

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
            model.addAttribute("courts", courtMapper.toBasicDTOs(courtService.findAll()));
            return "tournament_addMatch";
        }else {
            User currentUser = null;
        if(userDetails != null){
            String currentUsername = userDetails.getUsername();
            currentUser = userService.findByUsername(currentUsername);
        }
            Tournament currentTournament = tournamentService.findById(id);
            MatchDTO newMatch = new MatchDTO(
                    null,
                    userMapper.toBasicDTO(currentUser),
                    userMapper.toBasicDTO(winner),
                    userMapper.toBasicDTO(local),
                    userMapper.toBasicDTO(visitor),
                    courtMapper.toBasicDTO(courtObj),
                    result),

                    savedMatch;
                    savedMatch = matchService.save(newMatch);
            logger.info(savedMatch.toString());
            tournamentService.addMatch(id, savedMatch);
            return "redirect:/tournament/" + id;
        }
    }

    @GetMapping("/new")
    public String newTournament(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = null;
        if(userDetails != null){
            String currentUsername = userDetails.getUsername();
            currentUser = userService.findByUsername(currentUsername);
        }
        if(currentUser == null){
            model.addAttribute("errorMessage", "No se puede crear un torneo sin ser administrador");
            return "error";
        }
        if(!currentUser.isAdmin()){
            model.addAttribute("errorMessage", "Un usuario sin privilegios no puede crear un torneo");
            model.addAttribute("user", currentUser);
            return "error";
        }
        model.addAttribute("action", "new");
        model.addAttribute("actionName", "Crear ");
        model.addAttribute("user", currentUser);
        model.addAttribute("user", currentUser);
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

    @GetMapping("/{id}/modify")
    public String getModifyForm(Model model, @AuthenticationPrincipal UserDetails userDetails, @PathVariable long id){
        User currentUser = null;
        if(userDetails != null){
            String currentUsername = userDetails.getUsername();
            currentUser = userService.findByUsername(currentUsername);
        }
        if(currentUser == null || !currentUser.isAdmin()){
            model.addAttribute("errorMessage", "No se puede editar un torneo sin ser administrador");
            return "error";
        }

        Tournament tournament = tournamentService.findById(id);
        model.addAttribute("tournament", tournament);
        model.addAttribute("actionName", "Modificar ");
        model.addAttribute("action", id +"/modify");
        return "tournament_new";
    }

    @PostMapping("/{id}/modify")
    public String modifyTorunament(Model model, @AuthenticationPrincipal UserDetails userDetails, @PathVariable long id, String name, String initDate, String endDate, int price){
        User currentUser = null;
        if(userDetails != null){
            String currentUsername = userDetails.getUsername();
            currentUser = userService.findByUsername(currentUsername);
        }
        if(currentUser == null || !currentUser.isAdmin()){
            model.addAttribute("errorMessage", "No se ha podido modificar el torneo");
            return "error";
        }
        tournamentService.modify(id, name, initDate, endDate, price);
        return "redirect:/tournaments";
    }

    @GetMapping("/{id}/delete")
    public String deleteTournament(Model model, @AuthenticationPrincipal UserDetails userDetails, @PathVariable long id){
        User currentUser = null;
        if(userDetails != null){
            String currentUsername = userDetails.getUsername();
            currentUser = userService.findByUsername(currentUsername);
        }
        if(currentUser == null || !currentUser.isAdmin()){
            model.addAttribute("errorMessage", "No se ha podido eliminar el torneo");
            return "error";
        }
        tournamentService.delete(id);
        return "redirect:/tournaments";
    }
}
