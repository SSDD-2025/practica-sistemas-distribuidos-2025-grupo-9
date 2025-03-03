package es.urjc.club_tenis.controller;


import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

import es.urjc.club_tenis.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.urjc.club_tenis.model.Court;
import es.urjc.club_tenis.service.CourtService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/court")
public class CourtController {

    @Autowired
    private CourtService courtService;

    @GetMapping("/{id}")
    public String getCourts(Model model, @PathVariable long id, HttpSession session){
        if(session.getAttribute("user") == null){
            return "redirect:/login";
        }
        Court court = courtService.findById(id);

        model.addAttribute("court",court);
        model.addAttribute("user", session.getAttribute("user"));
        return "court";
    }

    @PostMapping("/{id}/book")
    public String bookCourt(Model model, @PathVariable long id, String date, String start, HttpSession session){
        if(session.getAttribute("user") == null){
            model.addAttribute("errorMessage", "No se puede reservar una pista sin estar registrado");
            return "error";
        }
        User currentUser = (User) session.getAttribute("user");
        LocalDate newDate = LocalDate.parse(date);
        LocalTime newStart = LocalTime.parse(start);
        Court court = courtService.findById(id);
        boolean invalid = false;
        if(court.getStart().isAfter(newStart) || court.getEnd().isBefore(newStart)){
            model.addAttribute("invalidStart", true);
            return getCourts(model, id, session);
        }
        if(!courtService.checkAvailability(court, newDate, newStart)){
            model.addAttribute("errorMessage", "La fecha ya esta ocupada.");
            return "error";
        }
        courtService.addReservation(currentUser, court, newDate, newStart);
        return "redirect:/";
    }
}
