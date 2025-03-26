package es.urjc.club_tenis.controller;

import es.urjc.club_tenis.service.CourtService;
import es.urjc.club_tenis.service.MatchService;
import es.urjc.club_tenis.model.*;
import es.urjc.club_tenis.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/match")
public class MatchController {

    @Autowired
    private MatchService matchService;

    @Autowired
    private CourtService courtService;

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public String getMatches(Model model, @PathVariable long id, HttpSession session){
        TennisMatch match = matchService.findById(id);
        model.addAttribute("match", match);
        User currentUser = (User) session.getAttribute("user");
        if(currentUser != null){
            if(currentUser.equals(match.getOwner()) || currentUser.isAdmin() || currentUser.equals(match.getLocal())){
                model.addAttribute("showModify", true);
            }
        }
        model.addAttribute("user", currentUser);
        return "match_details";
    }

    @GetMapping("/new")
    public String getForm(Model model, HttpSession session){
        if(session.getAttribute("user") == null){
            return "redirect:/login";
        }
        model.addAttribute("actionName", "Crear ");
        model.addAttribute("action", "");
        model.addAttribute("user", session.getAttribute("user"));
        model.addAttribute("courts", courtService.findAll());
        return "match_form";
    }

    @GetMapping("/{id}/delete")
    public String delete(Model model, @PathVariable long id, HttpSession session){
        User currentUser = (User) session.getAttribute("user");
        if(currentUser == null){
            return "redirect:/login";
        }
        TennisMatch match = matchService.findById(id);
        if(currentUser.equals(match.getOwner()) || currentUser.isAdmin() || currentUser.equals(match.getLocal())){
            //Delete
            matchService.delete(id);
            return "redirect:/matches";
        }else{
            model.addAttribute("errorMessage", "No tines permiso para eliminar este partido");
            return "error";
        }
    }

    @PostMapping("/")
    public String createMatch(String localUsername, String visitorUsername, long court, String winnerUsername, String result, Model model, HttpSession session){
        Court courtObj = courtService.findById(court);
        User local = userService.findByUsername(localUsername);
        User visitor = userService.findByUsername(visitorUsername);
        User winner = userService.findByUsername(winnerUsername);
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
            model.addAttribute("action", "");
            model.addAttribute("actionName", "Crear ");
            model.addAttribute("courts", courtService.findAll());
            return "match_form";
        }else {
            User currentUser = (User) session.getAttribute("user");
            if(currentUser == null){
                return "redirect:/login";
            }
            TennisMatch newMatch = matchService.createMatch(currentUser, local, visitor, courtObj, winner, result);
            return "redirect:/match/" + newMatch.getId();
        }
    }

    @GetMapping("/{id}/update")
    public String getUpdateForm(Model model, @PathVariable long id, HttpSession session){
        User currentUser = (User) session.getAttribute("user");
        if(currentUser == null){
            return "redirect:/login";
        }
        TennisMatch match = matchService.findById(id);

        if(currentUser.equals(match.getOwner()) || currentUser.isAdmin() || currentUser.equals(match.getLocal())){
            if(match.getWinner()==null){
                model.addAttribute("winner.username", "");
            }
            model.addAttribute("match", match);
            model.addAttribute("user", session.getAttribute("user"));
            model.addAttribute("courts", courtService.findAll());
            model.addAttribute("actionName", "Actualizar ");
            model.addAttribute("action", match.getId() + "/update");
            return "match_form";
        }else{
            model.addAttribute("errorMessage", "No tienes permiso para modificar este partido");
            return "error";
        }
    }

    @PostMapping("/{id}/update")
    public String udpateMatch(Model model, String localUsername, String visitorUsername, long court, String winnerUsername, String result, @PathVariable long id, HttpSession session){
        User currentUser = (User) session.getAttribute("user");
        if(currentUser == null){
            return "redirect:/login";
        }
        TennisMatch match = matchService.findById(id);
        if(currentUser.equals(match.getOwner()) || currentUser.isAdmin() || currentUser.equals(match.getLocal())){
            if(match.getWinner()==null){
                model.addAttribute("winner.username", "");
            }
            User local = userService.findByUsername(localUsername);
            User visitor = userService.findByUsername(visitorUsername);
            User winner = userService.findByUsername(winnerUsername);
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
                model.addAttribute("match", match);
                model.addAttribute("action", "");
                model.addAttribute("actionName", "Crear ");
                model.addAttribute("courts", courtService.findAll());
                return "match_form";
            }else {
                try{
                    TennisMatch updatedMatch = matchService.modify(id, local, visitor, courtService.findById(court), winner, result);
                    return "redirect:/match/" + updatedMatch.getId();
                }catch (ChangeSetPersister.NotFoundException e) {
                    model.addAttribute("errorMessage", "No se ha encontrado este partido");
                    return "error";
                }
            }
        }else{
            model.addAttribute("errorMessage", "No tines permiso para modificar este partido");
            return "error";
        }

    }

}
