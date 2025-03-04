package es.urjc.club_tenis.service;

import es.urjc.club_tenis.model.User;
import es.urjc.club_tenis.repositories.CourtRepository;
import es.urjc.club_tenis.model.Court;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.logging.Logger;

@Service
public class CourtService {

    Logger logger = Logger.getLogger("es.urjc.club_tenis.controller");

    @Autowired
    private CourtRepository repo;

    public Court save(Court court){
        return repo.save(court);
    }

    public Court findById(long id){
        return repo.findById(id).orElse(null);
    }

    public List<Court> findAll(){
        return repo.findAll();
    }

    public Court addReservation(User currentUser, Court court, LocalDate newDate, LocalTime newStart) {
        Court savedCourt = findById(court.getId());
        savedCourt.addReservation(currentUser, newDate, newStart);
        return repo.save(savedCourt);
    }

    public boolean checkAvailability(Court court, LocalDate newDate, LocalTime newStart) {
        return court.getReservations().get(LocalDateTime.of(newDate, newStart)) == null;
    }

    public void delete(Court court) {
        repo.delete(court);
    }
}
