package es.urjc.club_tenis.service;

import es.urjc.club_tenis.dto.court.CourtMapper;
import es.urjc.club_tenis.dto.match.*;
import es.urjc.club_tenis.dto.tournament.TournamentDTO;
import es.urjc.club_tenis.model.*;
import es.urjc.club_tenis.repositories.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class MatchService {

    @Autowired
    private MatchRepository repo;

    @Autowired
    private MatchMapper mapper;

    @Autowired
    private UserService userService;

    @Autowired
    private CourtService courtService;

    @Autowired
    private CourtMapper courtMapper;

    public Collection<MatchDTO> findAll() { return mapper.toDTOs(repo.findAll());}

    public Page<MatchDTO> findAll(int page){
        return repo.findAll(PageRequest.of(page - 1, TennisMatch.PAGE_SIZE)).map(mapper::toDTO);
    }

    public MatchDTO findById(long id) {
        return mapper.toDTO(repo.findById(id).orElseThrow());
    }

    public MatchDTO save(MatchDTO matchDTO){
        TennisMatch match = mapper.toDomain(matchDTO);
        Court managedCourt = courtMapper.toDomain(courtService.findById(match.getCourt().getId()));
        match.setCourt(managedCourt);

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
            Court managedCourt = courtMapper.toDomain(courtService.findById(updatedMatchDTO.court().id()));
            updatedMatch.setCourt(managedCourt);

            updatedMatch.setId(id);

            repo.save(updatedMatch);
            return mapper.toDTO(updatedMatch);
        }
        else throw new NoSuchElementException();
    }

    public void deleteUser(long id, User user){
        TennisMatch match = repo.findById(id).orElseThrow();
        if(match.getLocal().equals(user)){
            match.setLocal(User.DELETED_USER);
        }

        if(match.getVisitor().equals(user)){
            match.setVisitor(User.DELETED_USER);
        }

        if(match.getWinner().equals(user)){
            match.setWinner(User.DELETED_USER);
        }
        repo.save(match);
    }

}
