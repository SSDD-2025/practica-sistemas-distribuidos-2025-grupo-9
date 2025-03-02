package es.urjc.club_tenis.service;

import es.urjc.club_tenis.model.*;
import es.urjc.club_tenis.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class UserService {

    @Autowired
    private UserRepository repo;

    public User save(User user){
        return repo.save(user);
    }

    public User findByUsername(String username){
        return repo.findByUsername(username);
    }

    public List <User> findAll(){
        return repo.findAll();
    }

    public void addPlayedMatch(TennisMatch match){
        User local = match.getLocal();
        User visitor = match.getVisitor();
        local.playedTennisMatches.add(match);
        save(local);
        visitor.playedTennisMatches.add(match);
        save(visitor);
    }
}
