package es.urjc.club_tenis.service;

import es.urjc.club_tenis.model.TennisMatch;
import es.urjc.club_tenis.model.Tournament;
import es.urjc.club_tenis.model.User;
import es.urjc.club_tenis.repositories.TournamentRespository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.*;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.*;
import java.util.logging.Logger;

@Service
public class TournamentService {

    Logger logger = Logger.getLogger("es.urjc.club_tenis.controller");


    @Autowired
    private TournamentRespository repo;

    @Autowired
    private MatchService matchService;

    @Autowired
    private UserService userService;

    public Tournament save(Tournament tournament) {
        return repo.save(tournament);
    }

    public Tournament findById(long id) {
        return repo.findById(id).orElse(null);
    }

    public List<Tournament> findAll() {
        return repo.findAll();
    }

    @Transactional
    public void addMatch(Tournament tournament, TennisMatch match) {
        Tournament saved = findById(tournament.getId());
        TennisMatch savedMatch = matchService.findById(match.getId());
        saved.addMatch(savedMatch);
        save(saved);
    }

    @Transactional
    public List<TennisMatch> getMatches(Tournament t){
        return findById(t.getId()).getMatches();
    }

    public void modify(Tournament tournament, String name, String initDate, String endDate, int price) {
        Tournament saved = findById(tournament.getId());
        saved.setName(name);
        saved.setPrice(price);
        saved.setInitDate(LocalDate.parse(initDate));
        saved.setEndDate(LocalDate.parse(endDate));
        repo.save(saved);
    }

    public void delete(Tournament tournament) {
        Tournament saved = findById(tournament.getId());
        for(User u : saved.getParticipants()){
            userService.removeTournament(u, tournament);
        }
        List<TennisMatch> matches = getMatches(saved);
        saved.setMatches(null);
        logger.info("Numero de partidos del torneo: " + matches.size());
        for (TennisMatch t : matches) {
            matchService.detachTournament(t);
            matchService.delete(t);
        }
        saved.setMatches(null);
        repo.save(saved);
        repo.delete(saved);
    }
}
