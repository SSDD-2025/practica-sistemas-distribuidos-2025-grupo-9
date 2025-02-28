package es.urjc.club_tenis.service;

import es.urjc.club_tenis.model.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class DatabasePopulator {

    //Services
    @Autowired
    private UserService userService;
    @Autowired
    private MatchService matchService;
    @Autowired
    private TournamentService tournamentService;
    @Autowired
    private CourtService courtService;

    @PostConstruct
    public void init(){
        // Create dummys for demo version
        if(userService.findByUsername("admin") == null) {
            userService.save("admin", "Admin", "admin");
            userService.findByUsername("admin").setAdmin(true);
        }
        for(int i = 0; i < 10; i++){
            if(userService.findByUsername("user"+i) == null)
                userService.save("user"+i, "Usuario " + i, "user"+i);
        }

        for(int i = 0; i < 2; i++){
            if(tournamentService.findById(i+1) == null) {
                tournamentService.save("Tournament " + i, LocalDate.parse("2025-12-"+(12+i)), LocalDate.parse("2025-12-" +(15+i)), i);

            }
        }

    }

}
