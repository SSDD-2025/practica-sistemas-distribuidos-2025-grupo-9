package es.urjc.club_tenis.service;

import es.urjc.club_tenis.model.Tournament;
import es.urjc.club_tenis.repositories.TournamentRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.*;
import java.util.*;

@Service
public class TournamentService {

    @Autowired
    private TournamentRespository repo;

    public List<Tournament> findAll(){
        return repo.findAll();
    }
}
