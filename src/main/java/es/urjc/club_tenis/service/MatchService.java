package es.urjc.club_tenis.service;

import es.urjc.club_tenis.dto.match.*;
import es.urjc.club_tenis.model.*;
import es.urjc.club_tenis.repositories.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class MatchService {

    @Autowired
    private MatchRepository repo;

    @Autowired
    private MatchMapper mapper;

    @Autowired
    private TournamentService tournamentService;

    @Autowired
    private UserService userService;

    public List<MatchDTO> findAll() { return mapper.toDTOs(repo.findAll());}

    public MatchDTO findById(long id) {
        return mapper.toDTO(repo.findById(id).orElseThrow());
    }

    public MatchDTO save(MatchDTO matchDTO){
        TennisMatch match = mapper.toDomain(matchDTO);
        User localUser = userService.findByUsername(match.getLocal().getUsername());
        User visitorUser = userService.findByUsername(match.getVisitor().getUsername());

        if (localUser != null && visitorUser != null) {
            TennisMatch aux = repo.save(match);

            userService.addPlayedMatch(aux);
            return mapper.toDTO(aux);
        }
        return null;
    }

    public MatchDTO delete(long id) {
        TennisMatch saved = repo.findById(id).orElseThrow();
        User localUser = userService.findByUsername(saved.getLocal().getUsername());
        User visitorUser = userService.findByUsername(saved.getVisitor().getUsername());
        localUser.getPlayedMatches().remove(saved);
        visitorUser.getPlayedMatches().remove(saved);
        userService.save(localUser);
        userService.save(visitorUser);
        repo.deleteById(id);
        return mapper.toDTO(saved);
    }

    public MatchDTO modify(long id, MatchDTO updatedMatchDTO){
        if(repo.existsById(id)){
            TennisMatch updatedMatch = mapper.toDomain(updatedMatchDTO);
            updatedMatch.setId(id);

            repo.save(updatedMatch);
            return mapper.toDTO(updatedMatch);
        }
        else throw new NoSuchElementException();
    }

    public void deleteUser(long id, User user){
        TennisMatch match = repo.findById(id).orElseThrow();
        User deleted = userService.findByUsername("deleted_user");
        if(match.getLocal().equals(user)){
            match.setLocal(deleted);
        }

        if(match.getVisitor().equals(user)){
            match.setVisitor(deleted);
        }

        if(match.getWinner().equals(user)){
            match.setWinner(deleted);
        }
        repo.save(match);
    }

}
