package es.urjc.club_tenis.service;

import es.urjc.club_tenis.repositories.CourtRepository;
import es.urjc.club_tenis.model.Court;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.*;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.*;

@Service
public class CourtService {

    @Autowired
    private CourtRepository repo;

    public Court save(Court court){
        return repo.save(court);
    }

    public Court findById(long id){
        return repo.findById(id).orElse(null);
    }

    public List<Court> findAll(){
        return repo.findAll();
    }

}
