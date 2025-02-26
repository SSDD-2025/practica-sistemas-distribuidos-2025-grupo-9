package es.urjc.club_tenis.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/court")
public class CourtController {

    @GetMapping("/")
    public String getCourts(Model model, @PathVariable long id){
        return "court";
    }
}
