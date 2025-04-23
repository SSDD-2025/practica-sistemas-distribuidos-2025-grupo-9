package es.urjc.club_tenis.controller.web;

import es.urjc.club_tenis.dto.court.CourtMapper;
import es.urjc.club_tenis.dto.match.MatchDTO;
import es.urjc.club_tenis.dto.user.UserMapper;
import es.urjc.club_tenis.service.*;
import es.urjc.club_tenis.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/match")
public class MatchController {

    @Autowired
    private MatchService matchService;

    @Autowired
    private CourtService courtService;

    @Autowired
    private UserService userService;

    //Temporal hasta que se use UserDTO y CourtDTO aquí
    @Autowired
    private UserMapper userMapper;

    @GetMapping("/{id}")
    public String getMatches(Model model, @PathVariable long id, @AuthenticationPrincipal UserDetails userDetails){
        MatchDTO match = matchService.findById(id);
        model.addAttribute("match", match);
        User currentUser = null;
        if(userDetails != null){
            String currentUsername = userDetails.getUsername();
            currentUser = userService.findByUsername(currentUsername);
        }
        if(currentUser != null){
            if(userMapper.toDTO(currentUser).id().equals(match.owner().id()) || currentUser.isAdmin() || userMapper.toDTO(currentUser).id().equals(match.local().id())){
                model.addAttribute("showModify", true);
            }
        }
        model.addAttribute("user", currentUser);
        return "match_details";
    }

    @GetMapping("/new")
    public String getForm(Model model, @AuthenticationPrincipal UserDetails userDetails){
        if(userDetails == null){
            return "redirect:/login";
        }
        String currentUsername = userDetails.getUsername();
        User currentUser = userService.findByUsername(currentUsername);
        model.addAttribute("actionName", "Crear ");
        model.addAttribute("action", "");
        model.addAttribute("user", currentUser);
        model.addAttribute("courts", courtService.findAllDTOs());
        return "match_form";
    }

    @GetMapping("/{id}/delete")
    public String delete(Model model, @PathVariable long id,@AuthenticationPrincipal UserDetails userDetails){
        if(userDetails == null){
            return "redirect:/login";
        }
        String currentUsername = userDetails.getUsername();
        User currentUser = userService.findByUsername(currentUsername);
        MatchDTO match = matchService.findById(id);
        if(userMapper.toDTO(currentUser).id().equals(match.owner().id()) || currentUser.isAdmin() || userMapper.toDTO(currentUser).id().equals(match.local().id())){
            //Delete
            matchService.delete(id);
            return "redirect:/matches";
        }else{
            model.addAttribute("errorMessage", "No tines permiso para eliminar este partido");
            return "error";
        }
    }

    @PostMapping("/")
    public String createMatch(String localUsername, String visitorUsername, long court, String winnerUsername, String result, Model model, @AuthenticationPrincipal UserDetails userDetails){
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
            model.addAttribute("courts", courtService.findAllDTOs());
            return "match_form";
        }else {
            User currentUser = null;
            if(userDetails != null){
                String currentUsername = userDetails.getUsername();
                currentUser = userService.findByUsername(currentUsername);
            }
            if(currentUser == null){
                return "redirect:/login";
            }
            MatchDTO newMatch = new MatchDTO(
                    null,
                    userMapper.toBasicDTO(currentUser),
                    userMapper.toBasicDTO(winner),
                    userMapper.toBasicDTO(local),
                    userMapper.toBasicDTO(visitor),
                    courtService.findById(court),
                    result),

                    savedMatch;

            savedMatch = matchService.save(newMatch);

            return "redirect:/match/" + savedMatch.id();
        }
    }

    @GetMapping("/{id}/update")
    public String getUpdateForm(Model model, @PathVariable long id, @AuthenticationPrincipal UserDetails userDetails){
        User currentUser = null;
        if(userDetails != null){
            String currentUsername = userDetails.getUsername();
            currentUser = userService.findByUsername(currentUsername);
        }
        if(currentUser == null){
            return "redirect:/login";
        }
        MatchDTO match = matchService.findById(id);

        if(userMapper.toDTO(currentUser).id().equals(match.owner().id()) || currentUser.isAdmin() || userMapper.toDTO(currentUser).id().equals(match.local().id())){
            if(match.winner()==null){
                model.addAttribute("winner.username", "");
            }
            model.addAttribute("match", match);
            model.addAttribute("user", currentUser);
            model.addAttribute("courts", courtService.findAllDTOs());
            model.addAttribute("actionName", "Actualizar ");
            model.addAttribute("action", match.id() + "/update");
            return "match_form";
        }else{
            model.addAttribute("errorMessage", "No tienes permiso para modificar este partido");
            return "error";
        }
    }

    @PostMapping("/{id}/update")
    public String udpateMatch(Model model, String localUsername, String visitorUsername, long court, String winnerUsername, String result, @PathVariable long id, @AuthenticationPrincipal UserDetails userDetails){
        User currentUser = null;
        if(userDetails != null){
            String currentUsername = userDetails.getUsername();
            currentUser = userService.findByUsername(currentUsername);
        }
        if(currentUser == null){
            return "redirect:/login";
        }
        MatchDTO match = matchService.findById(id);
        if(userMapper.toDTO(currentUser).id().equals(match.owner().id()) || currentUser.isAdmin() || userMapper.toDTO(currentUser).id().equals(match.local().id())){
            if(match.winner()==null){
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
                model.addAttribute("actionName", "Actualizar ");
                model.addAttribute("courts", courtService.findAllDTOs());
                return "match_form";
            }else {
                MatchDTO modifiedMatch = new MatchDTO(
                        id,
                        match.owner(),
                        userMapper.toBasicDTO(winner),
                        userMapper.toBasicDTO(local),
                        userMapper.toBasicDTO(visitor),
                        match.court(),
                        result),

                        updatedMatch;

                updatedMatch = matchService.modify(id, modifiedMatch);
                return "redirect:/match/" + updatedMatch.id();
            }
        }else{
            model.addAttribute("errorMessage", "No tines permiso para modificar este partido");
            return "error";
        }

    }

}
