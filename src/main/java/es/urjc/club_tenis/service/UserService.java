package es.urjc.club_tenis.service;

import es.urjc.club_tenis.model.*;
import es.urjc.club_tenis.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository repo;

    public User save(String username, String name, String password){
        User user = new User(username, name, password);
        return repo.save(user);
    }

    public User findByUsername(String username){
        return repo.findByUsername(username);
    }
}
