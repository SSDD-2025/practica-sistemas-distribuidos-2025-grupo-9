package es.urjc.club_tenis.service;

import es.urjc.club_tenis.model.TennisMatch;
import es.urjc.club_tenis.model.User;
import es.urjc.club_tenis.repositories.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MatchService {

    @Autowired
    private MatchRepository repo;

    @Autowired
    private UserService userService;

    public List<TennisMatch> findAll() {
        return repo.findAll();
    }

    public TennisMatch save(TennisMatch match){
        userService.addPlayedMatch(match);
        return repo.save(match);
    }

    public TennisMatch findById(long id) {
        return repo.findById(id).orElse(null);
    }
}
