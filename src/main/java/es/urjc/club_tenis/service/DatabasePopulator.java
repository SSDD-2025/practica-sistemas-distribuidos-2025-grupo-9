package es.urjc.club_tenis.service;

import es.urjc.club_tenis.model.*;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.OffsetScrollPositionHandlerMethodArgumentResolver;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.logging.Logger;

@Service
public class DatabasePopulator {

    @Autowired
    private UserService userService;
    @Autowired
    private MatchService matchService;
    @Autowired
    private TournamentService tournamentService;
    @Autowired
    private CourtService courtService;

    Logger logger = Logger.getLogger("es.urjc.club_tenis.controller");


    @PostConstruct
    public void init(){
        // Create dummys for demo version

        //Users
        List<User> users = userService.findAll();
        User admin = userService.findByUsername("admin");
        User deleted = userService.findByUsername("deleted_user");
        if(admin == null) {
            admin = new User("admin", "Admin", "admin", true);
            userService.save(admin);
            userService.findByUsername("admin").setAdmin(true);
        }
        if(deleted == null){
            deleted = new User("deleted_user","Deleted","deleted",null);
            userService.save(deleted);
        }else{
            users.remove(deleted);
        }
        for(int i = 0; i < 10; i++){
            if(userService.findByUsername("user"+i) == null){
                User newUser = new User("user"+i, "Usuario " + i, "user"+i, null);
                users.add(userService.save(newUser));
            }
        }

        //Courts
        if(courtService.findAll().isEmpty()){
            for(int i = 0; i < 2; i++){
                courtService.save(new Court("Court"+i, (float) (Math.random()*10), LocalTime.of(11,00), LocalTime.of(20,00)));
            }
        }

        // Tournaments
        for (int i = 0; i < 3; i++) {
            if (tournamentService.findById(i + 1) == null) {
                Tournament t = new Tournament("Tournament " + i, LocalDate.parse("2025-12-" + (12 + i)), LocalDate.parse("2025-12-" + (15 + i)), (int) (Math.random()*10));
                Tournament saved = tournamentService.save(t);
            }
        }


        //Matches
        if(matchService.findAll().isEmpty()) {
            for(int i = 0; i < 10; i++){
                User local = users.get((int) (Math.random() * users.size()));
                User visitor = users.get((int) (Math.random() * users.size()));
                User winner = Math.random() > 0.5? local: visitor;
                TennisMatch match = new TennisMatch(admin, local, visitor, winner, "3-6, 6-4, 7-5", courtService.findAll().getFirst());
                matchService.save(match);
            }
        }
    }

}
