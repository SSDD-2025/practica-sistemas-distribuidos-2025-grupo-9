package es.urjc.club_tenis.service;

import es.urjc.club_tenis.dto.court.CourtBasicDTO;
import es.urjc.club_tenis.dto.court.CourtDTO;
import es.urjc.club_tenis.model.User;
import es.urjc.club_tenis.repositories.CourtRepository;
import es.urjc.club_tenis.model.Court;
import es.urjc.club_tenis.dto.court.CourtMapper;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class CourtService {

    @Autowired
    private CourtRepository repo;

    @Autowired
    private CourtMapper mapper;

    public CourtDTO save(Court court){
        return mapper.toDTO(repo.save(court));
    }

    public CourtDTO findById(long id){
        return mapper.toDTO(repo.findById(id).orElse(null));
    }

    public List<CourtDTO> findAllDTOs(){
        return mapper.toDTOs(repo.findAll());
    }

    public List<Court> findAll(){
        return repo.findAll();
    }

    public Page<CourtDTO> findAll(int page){
        return repo.findAll(PageRequest.of(page - 1, Court.PAGE_SIZE)).map(mapper::toDTO);
    }

    public Court addReservation(User currentUser, Court court, LocalDate newDate, LocalTime newStart) {
        Court savedCourt = repo.findById(court.getId()).orElse(null);
        savedCourt.addReservation(currentUser, newDate, newStart);
        return repo.save(savedCourt);
    }

    public boolean checkAvailability(Court court, LocalDate newDate, LocalTime newStart) {
        return court.getReservations().get(LocalDateTime.of(newDate, newStart)) == null;
    }

    public void delete(Court court) {
        repo.delete(court);
    }

    public void delete(long id) {
        Court court = repo.findById(id).orElse(null);
        delete(court);
    }

    public CourtDTO update(Court court) {
        return mapper.toDTO(repo.save(court));
    }
}
