package es.urjc.club_tenis.service;

import es.urjc.club_tenis.model.TennisMatch;
import es.urjc.club_tenis.model.Tournament;
import es.urjc.club_tenis.model.User;
import es.urjc.club_tenis.repositories.TournamentRespository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.*;

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
    public void addMatch(long id, TennisMatch match) {
        Tournament saved = findById(id);
        TennisMatch savedMatch = matchService.findById(match.getId());
        saved.getMatches().add(savedMatch);
        addParticipant(id, userService.findByUsername(savedMatch.getLocal().getUsername()));
        addParticipant(id,userService.findByUsername(savedMatch.getVisitor().getUsername()));
        save(saved);
    }

    @Transactional
    public void addParticipant(long id, User user){
        Tournament saved = findById(id);
        saved.getParticipants().add(user);
        save(saved);
    }

    public void modify(long id, String name, String initDate, String endDate, int price) {
        Tournament saved = findById(id);
        saved.setName(name);
        saved.setPrice(price);
        saved.setInitDate(LocalDate.parse(initDate));
        saved.setEndDate(LocalDate.parse(endDate));
        repo.save(saved);
    }

    @Transactional
    public void removeParticipant(long id, User user){
        Tournament saved = findById(id);
        Set<User> users = new HashSet<>(saved.getParticipants());
        users.remove(user);
        saved.setParticipants(users);
        save(saved);
    }


    @Transactional
    public void removeParticipants(long id){
        Tournament t = findById(id);
        Set<User> users = new HashSet<>(t.getParticipants());
        for(User u : users){
            userService.removeTournament(u.getUsername(), t);
        }
        t.getParticipants().clear();
        save(t);
    }

    @Transactional
    public void removeMatches(long id){
        Tournament t = findById(id);
        Set<TennisMatch> matches = new HashSet<>(t.getMatches());
        for (TennisMatch m : matches) {
            matchService.detachTournament(id);
            matchService.delete(id);
        }
        t.getMatches().clear();
        save(t);
    }

    @Transactional
    public void delete(long id) {
        Tournament saved = findById(id);
        if (saved != null) {
            removeParticipants(id);
            removeMatches(id);
            save(saved);
            repo.deleteById(id);
        }
    }
}
