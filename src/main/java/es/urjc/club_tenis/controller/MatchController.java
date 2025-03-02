package es.urjc.club_tenis.controller;

import es.urjc.club_tenis.service.MatchService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/match")
public class MatchController {

    @Autowired
    private MatchService matchService;

    @GetMapping("/{id}")
    public String getMatches(Model model, @PathVariable long id, HttpSession session){
        model.addAttribute("match", matchService.findById(id));
        model.addAttribute("user", session.getAttribute("user"));
        return "match_details";
    }

}
