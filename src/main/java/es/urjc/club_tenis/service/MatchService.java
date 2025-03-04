package es.urjc.club_tenis.service;

import es.urjc.club_tenis.model.Court;
import es.urjc.club_tenis.model.TennisMatch;
import es.urjc.club_tenis.model.Tournament;
import es.urjc.club_tenis.model.User;
import es.urjc.club_tenis.repositories.MatchRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.resource.ResourceUrlProvider;

import java.util.List;
import java.util.logging.Logger;

@Service
public class MatchService {

    Logger logger = Logger.getLogger("es.urjc.club_tenis.controller");

    @Autowired
    private MatchRepository repo;

    @Autowired
    private TournamentService tournamentService;

    @Autowired
    private UserService userService;
    @Autowired
    private ResourceUrlProvider mvcResourceUrlProvider;

    public List<TennisMatch> findAll() {
        return repo.findAll();
    }

    @Transactional
    public TennisMatch save(TennisMatch match){
        User localUser = userService.findByUsername(match.getLocal().getUsername());
        User visitorUser = userService.findByUsername(match.getVisitor().getUsername());

        if (localUser != null && visitorUser != null) {
            TennisMatch aux = repo.save(match);

            userService.addPlayedMatch(aux);
            return aux;
        }
        return null;
    }

    public TennisMatch findById(long id) {
        return repo.findById(id).orElse(null);
    }

    public void delete(TennisMatch match) {
        TennisMatch saved = findById(match.getId());
        User localUser = userService.findByUsername(saved.getLocal().getUsername());
        User visitorUser = userService.findByUsername(saved.getVisitor().getUsername());
        localUser.getPlayedMatches().remove(saved);
        visitorUser.getPlayedMatches().remove(saved);
        userService.save(localUser);
        userService.save(visitorUser);
        detachTournament(match, match.getTournament());
        repo.delete(saved);
    }

    public TennisMatch createMatch(User local, User visitor, Court courtObj, User winner, String result) {
        TennisMatch newMatch = new TennisMatch(local, visitor, courtObj, winner, result);
        userService.addPlayedMatch(newMatch);
        return repo.save(newMatch);
    }

    public TennisMatch createMatch(User owner, User local, User visitor, Court courtObj, User winner, String result) {
        TennisMatch newMatch = new TennisMatch(owner, local, visitor,  winner, result, courtObj);
        userService.addPlayedMatch(newMatch);
        return repo.save(newMatch);
    }

    public TennisMatch modify(TennisMatch oldMatch, User local, User visitor, Court court, User winner, String result) throws ChangeSetPersister.NotFoundException {
        TennisMatch match = findById(oldMatch.getId());
        if(match == null){
            throw new ChangeSetPersister.NotFoundException();
        }
        match.setCourt(court);
        match.setLocal(local);
        match.setVisitor(visitor);
        match.setWinner(winner);
        match.setResult(result);
        return repo.save(match);
    }

    public void deleteUser(User user, TennisMatch match){

        User deleted = userService.findByUsername("deleted_user");
        if(match.getLocal().equals(user)){
            match.setLocal(deleted);
        }else{
            match.setVisitor(deleted);
        }

        if(match.getWinner().equals(user)){
            match.setWinner(deleted);
        }
        repo.save(match);
    }

    public TennisMatch createMatch(User currentUser, User local, User visitor, Court courtObj, User winner, String result, Tournament currentTournament) {
        TennisMatch newMatch = new TennisMatch(currentUser, local, visitor,  winner, result, courtObj);
        newMatch.setTournament(tournamentService.findById(currentTournament.getId()));
        userService.addPlayedMatch(newMatch);
        TennisMatch savedMatch = save(newMatch);
        return savedMatch;
    }

    public void detachTournament(TennisMatch t, Tournament tournament) {
        if(t.getTournament() != null){
            TennisMatch saved = findById(t.getId());
            Tournament savedTournament = tournamentService.findById(tournament.getId());
            saved.setTournament(null);
            save(saved);
            tournament.getMatches().remove(saved);
            tournamentService.save(savedTournament);
        }
    }
}
