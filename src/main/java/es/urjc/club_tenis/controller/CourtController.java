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

    @GetMapping("/new")
    public String getNewCourtForm(Model model, HttpSession session){
        User currentUser = (User) session.getAttribute("user");
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
    public String createNewCourt(Model model, HttpSession session, String name, float price , String start, String end){
        User currentUser = (User) session.getAttribute("user");
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
    public String getModifyForm(Model model, HttpSession session, @PathVariable long id){
        User currentUser = (User) session.getAttribute("user");
        if(currentUser == null || !currentUser.isAdmin()){
            model.addAttribute("errorMessage", "No se puede reservar una pista sin estar registrado");
            return "error";
        }
        model.addAttribute("user", currentUser);
        model.addAttribute("court", courtService.findById(id));
        model.addAttribute("action", id);
        model.addAttribute("actionName", "Modificar ");
        return "court_form";
    }

    @PostMapping("/{id}")
    public String updateCourt(Model model, HttpSession session, @PathVariable long id, String name, float price , String start, String end){
        User currentUser = (User) session.getAttribute("user");
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
    public String deleteCourt(Model model, HttpSession session, @PathVariable long id){
        User currentUser = (User) session.getAttribute("user");
        model.addAttribute("user", currentUser);
        if(currentUser == null || !currentUser.isAdmin()) {
            model.addAttribute("errorMessage", "No se puede borrar una pista sin ser administrador");
            return "error";
        }
        courtService.delete(courtService.findById(id));
        return "redirect:/courts";
    }


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
        model.addAttribute("user", currentUser);
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
