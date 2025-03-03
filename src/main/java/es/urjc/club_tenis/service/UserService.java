package es.urjc.club_tenis.service;

import es.urjc.club_tenis.model.*;
import es.urjc.club_tenis.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.*;
import java.util.logging.Logger;

@Service
public class UserService {

    Logger logger = Logger.getLogger("es.urjc.club_tenis.controller");


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

    @Transactional
    public void addPlayedMatch(TennisMatch match, User user){
        User savedUser = findByUsername(user.getUsername());
        logger.info(savedUser.toString());
        if(savedUser.equals(user)){
            logger.info(savedUser.getPlayedMatches().toString());
            if(savedUser.playedTennisMatches == null){
                savedUser.playedTennisMatches = new ArrayList<>();
            }
            savedUser.playedTennisMatches.add(match);
            save(savedUser);
        }
    }

    public User modify(User oldUser, String newUsername, String newName) throws ChangeSetPersister.NotFoundException {
        User user = findByUsername(oldUser.username);
        if(user == null){
            throw new ChangeSetPersister.NotFoundException();
        }
        user.setUsername(newUsername);
        user.setName(newName);
        return repo.save(user);
    }
}
