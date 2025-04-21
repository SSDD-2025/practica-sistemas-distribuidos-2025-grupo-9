package es.urjc.club_tenis.controller.web;


import java.time.LocalDate;
import java.time.LocalTime;

import es.urjc.club_tenis.dto.court.CourtMapper;
import es.urjc.club_tenis.model.User;
import es.urjc.club_tenis.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import es.urjc.club_tenis.model.Court;
import es.urjc.club_tenis.service.CourtService;

@Controller
@RequestMapping("/court")
public class CourtController {

    @Autowired
    private UserService userService;

    @Autowired
    private CourtService courtService;

    @Autowired
    private CourtMapper courtMapper;

    @GetMapping("/new")
    public String getNewCourtForm(Model model, @AuthenticationPrincipal UserDetails userDetails){
        User currentUser = null;
        if(userDetails != null){
            String currentUsername = userDetails.getUsername();
            currentUser = userService.findByUsername(currentUsername);
        }
        if(currentUser == null){
            model.addAttribute("errorMessage", "No se puede reservar una pista sin estar registrado");
            return "error";
        }
        if(!currentUser.isAdmin()){
            model.addAttribute("errorMessage", "Un usuario sin privilegios no puede crear una pista");
            model.addAttribute("user", currentUser);
            return "error";
        }
        model.addAttribute("actionName", "Crear");
        model.addAttribute("action", "");
        return "court_form";
    }

    @PostMapping("/")
    public String createNewCourt(Model model, @AuthenticationPrincipal UserDetails userDetails, String name, float price , String start, String end){
        User currentUser = null;
        if(userDetails != null){
            String currentUsername = userDetails.getUsername();
            currentUser = userService.findByUsername(currentUsername);
        }
        model.addAttribute("user", currentUser);
        if(currentUser == null){
            model.addAttribute("errorMessage", "No se puede reservar una pista sin estar registrado");
            return "error";
        }
        if(!currentUser.isAdmin()){
            model.addAttribute("errorMessage", "Un usuario sin privilegios no puede crear una pista");
            model.addAttribute("user", currentUser);
            return "error";
        }
        Court newCourt = new Court(name, price, LocalTime.parse(start), LocalTime.parse(end));
        courtService.save(newCourt);
        return "redirect:/courts";
    }

    @GetMapping("/{id}/modify")
    public String getModifyForm(Model model, @AuthenticationPrincipal UserDetails userDetails, @PathVariable long id){
        User currentUser = null;
        if(userDetails != null){
            String currentUsername = userDetails.getUsername();
            currentUser = userService.findByUsername(currentUsername);
        }
        if(currentUser == null || !currentUser.isAdmin()){
            model.addAttribute("errorMessage", "No se puede reservar una pista sin estar registrado");
            return "error";
        }
        model.addAttribute("user", currentUser);
        model.addAttribute("court", courtMapper.toDTO(courtService.findById(id)));
        model.addAttribute("action", id);
        model.addAttribute("actionName", "Modificar ");
        return "court_form";
    }

    @PostMapping("/{id}")
    public String updateCourt(Model model, @AuthenticationPrincipal UserDetails userDetails, @PathVariable long id, String name, float price , String start, String end){
        User currentUser = null;
        if(userDetails != null){
            String currentUsername = userDetails.getUsername();
            currentUser = userService.findByUsername(currentUsername);
        }
        model.addAttribute("user", currentUser);
        if(currentUser == null){
            model.addAttribute("errorMessage", "No se puede reservar una pista sin estar registrado");
            return "error";
        }
        if(!currentUser.isAdmin()){
            model.addAttribute("errorMessage", "Un usuario sin privilegios no puede crear una pista");
            model.addAttribute("user", currentUser);
            return "error";
        }
        Court court = courtService.findById(id);
        court.setName(name);
        court.setPrice(price);
        court.setStart(LocalTime.parse(start));
        court.setEnd(LocalTime.parse(end));
        courtService.save(court);
        return "redirect:/courts";
    }

    @GetMapping("/{id}/delete")
    public String deleteCourt(Model model, @AuthenticationPrincipal UserDetails userDetails, @PathVariable long id){
        User currentUser = null;
        if(userDetails != null){
            String currentUsername = userDetails.getUsername();
            currentUser = userService.findByUsername(currentUsername);
        }
        model.addAttribute("user", currentUser);
        if(currentUser == null || !currentUser.isAdmin()) {
            model.addAttribute("errorMessage", "No se puede borrar una pista sin ser administrador");
            return "error";
        }
        courtService.delete(courtService.findById(id));
        return "redirect:/courts";
    }


    @GetMapping("/{id}")
    public String getCourts(Model model, @PathVariable long id, @AuthenticationPrincipal UserDetails userDetails){
        if(userDetails == null){
            return "redirect:/login";
        }
        Court court = courtService.findById(id);
        String currentUsername = userDetails.getUsername();
        User currentUser = userService.findByUsername(currentUsername);
        model.addAttribute("court",courtMapper.toDTO(court));
        model.addAttribute("user", currentUser);
        return "court";
    }

    @PostMapping("/{id}/book")
    public String bookCourt(Model model, @PathVariable long id, String date, String start, @AuthenticationPrincipal UserDetails userDetails){
        if(userDetails == null){
            model.addAttribute("errorMessage", "No se puede reservar una pista sin estar registrado");
            return "error";
        }

        String currentUsername = userDetails.getUsername();
        User currentUser = userService.findByUsername(currentUsername);

        model.addAttribute("user", currentUser);
        LocalDate newDate = LocalDate.parse(date);
        LocalTime newStart = LocalTime.parse(start);
        Court court = courtService.findById(id);
        boolean invalid = false;
        if(court.getStart().isAfter(newStart) || court.getEnd().isBefore(newStart)){
            model.addAttribute("invalidStart", true);
            return getCourts(model, id, userDetails);
        }
        if(!courtService.checkAvailability(court, newDate, newStart)){
            model.addAttribute("errorMessage", "La fecha ya esta ocupada.");
            return "error";
        }
        courtService.addReservation(currentUser, court, newDate, newStart);
        return "redirect:/";
    }
}
