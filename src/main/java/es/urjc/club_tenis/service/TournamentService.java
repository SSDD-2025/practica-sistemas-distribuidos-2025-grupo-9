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

    public Tournament save(Tournament tournament) {
        return repo.save(tournament);
    }

    public Tournament findById(long id) {
        return repo.findById(id).orElse(null);
    }

    public List<Tournament> findAll(){
        return repo.findAll();
    }

    public void modify(Tournament tournament, String name, String initDate, String endDate, int price) {
        Tournament saved = findById(tournament.getId());
        saved.setName(name);
        saved.setPrice(price);
        saved.setInitDate(LocalDate.parse(initDate));
        saved.setEndDate(LocalDate.parse(endDate));
        repo.save(saved);
    }
}
