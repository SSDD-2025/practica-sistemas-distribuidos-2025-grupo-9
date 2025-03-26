package es.urjc.club_tenis.service;

import es.urjc.club_tenis.model.Court;
import es.urjc.club_tenis.model.TennisMatch;
import es.urjc.club_tenis.model.Tournament;
import es.urjc.club_tenis.model.User;
import es.urjc.club_tenis.repositories.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MatchService {

    @Autowired
    private MatchRepository repo;

    @Autowired
    private TournamentService tournamentService;

    @Autowired
    private UserService userService;

    public List<TennisMatch> findAll() {
        return repo.findAll();
    }

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

    public void delete(long id) {
        TennisMatch saved = findById(id);
        User localUser = userService.findByUsername(saved.getLocal().getUsername());
        User visitorUser = userService.findByUsername(saved.getVisitor().getUsername());
        localUser.getPlayedMatches().remove(saved);
        visitorUser.getPlayedMatches().remove(saved);
        userService.save(localUser);
        userService.save(visitorUser);
        detachTournament(id);
        repo.delete(saved);
    }

    public TennisMatch createMatch(User local, User visitor, Court courtObj, User winner, String result) {
        TennisMatch newMatch = new TennisMatch(local, visitor, courtObj, winner, result);
        return save(newMatch);
    }

    public TennisMatch createMatch(User owner, User local, User visitor, Court courtObj, User winner, String result) {
        TennisMatch newMatch = new TennisMatch(owner, local, visitor,  winner, result, courtObj);
        return save(newMatch);
    }

    public TennisMatch modify(long id, User local, User visitor, Court court, User winner, String result) throws ChangeSetPersister.NotFoundException {
        TennisMatch match = findById(id);
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

    public void deleteUser(long id, User user){
        TennisMatch match = findById(id);
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
        return save(newMatch);
    }

    public void detachTournament(long id) {
        TennisMatch saved = findById(id);
        if(saved.getTournament() != null){
            Tournament savedTournament = tournamentService.findById(saved.getTournament().getId());
            saved.setTournament(null);
            save(saved);
            savedTournament.getMatches().remove(saved);
            tournamentService.save(savedTournament);
        }
    }
}
