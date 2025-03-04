package es.urjc.club_tenis.service;

import es.urjc.club_tenis.model.TennisMatch;
import es.urjc.club_tenis.model.Tournament;
import es.urjc.club_tenis.model.User;
import es.urjc.club_tenis.repositories.TournamentRespository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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

    @PersistenceContext
    private EntityManager entityManager;

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
        matchService.save(saved.addMatch(savedMatch));
        save(saved);
    }

    @Transactional
    public Set<TennisMatch> getMatches(Tournament t){
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

    public Set<User> getParticipants(Tournament t){
        return findById(t.getId()).getParticipants();
    }

    public void removeParticipants(Tournament t){
        Set<User> users = new HashSet<>(getParticipants(t));
        logger.info("Users " + users);
        for(User u : users){
            userService.removeTournament(u, t);
        }
        save(t);
    }

    public void removeMatches(Tournament t){
        Set<TennisMatch> matches = new HashSet<>(getMatches(t));
        for (TennisMatch m : matches) {
            matchService.detachTournament(m);
            matchService.delete(m);
        }
        save(t);
    }

    @Transactional
    public void delete(Tournament tournament) {
        Tournament saved = findById(tournament.getId());
        repo.delete(saved);
    }
}
