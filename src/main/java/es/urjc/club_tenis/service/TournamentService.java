package es.urjc.club_tenis.service;

import es.urjc.club_tenis.model.Tournament;
import es.urjc.club_tenis.repositories.TournamentRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.*;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.*;

@Service
public class TournamentService {

    @Autowired
    private TournamentRespository repo;

    public Tournament save(String name, LocalDate initDate, LocalDate endDate, int price) {

        Tournament newTournament = new Tournament(name, initDate, endDate, price);
        return repo.save(newTournament);
    }

    public Tournament findById(long id) { return repo.findById(id); }

    public List<Tournament> findAll(){
        return repo.findAll();
    }
}
