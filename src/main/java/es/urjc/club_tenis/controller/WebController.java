package es.urjc.club_tenis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.service.annotation.GetExchange;

@Controller
public class WebController {

    @GetMapping("/profile")
    public String getProfilePage(Model model){
        return "profile";
    }
}
