package es.urjc.club_tenis.controller;


import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/court")
public class CourtController {

    @GetMapping("/")
    public String getCourts(Model model/*, @PathVariable long id*/){
        return "court";
    }

    @PostMapping("/book")
    public String bookCourt(Model model, String date, String start, String end){

        LocalDate newDate = LocalDate.parse(date);
        LocalTime newStart = LocalTime.parse(start);
        LocalTime newEnd = LocalTime.parse(end);

        return "redirect:/";
    }
}
