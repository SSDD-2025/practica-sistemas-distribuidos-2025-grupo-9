package es.urjc.club_tenis.service;

import es.urjc.club_tenis.dto.match.*;
import es.urjc.club_tenis.dto.tournament.TournamentDTO;
import es.urjc.club_tenis.dto.tournament.TournamentMapper;
import es.urjc.club_tenis.model.TennisMatch;
import es.urjc.club_tenis.model.Tournament;
import es.urjc.club_tenis.model.User;
import es.urjc.club_tenis.repositories.TournamentRespository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.*;

import java.time.LocalDate;
import java.util.*;
import java.util.logging.Logger;
@Service
public class TournamentService {

    Logger logger = Logger.getLogger("es.urjc.club_tenis.controller");

    @Autowired
    private TournamentRespository repo;

    @Autowired
    private MatchService matchService;

    @Autowired
    private MatchMapper matchMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private TournamentMapper tournamentMapper;

    public TournamentDTO save(TournamentDTO dto) {
        Tournament tournament = tournamentMapper.toDomain(dto);
        return tournamentMapper.toDto(repo.save(tournament));
    }

    public TournamentDTO findById(long id) {
        return repo.findById(id)
                .map(tournamentMapper::toDto)
                .orElse(null);
    }

    public List<TournamentDTO> findAll() {
        return tournamentMapper.toDTOs(repo.findAll());
    }

    @Transactional
    public void addMatch(long id, MatchDTO match) {
        Tournament saved = repo.findById(id).orElseThrow();
        TennisMatch savedMatch = matchMapper.toDomain(matchService.findById(match.id()));
        saved.getMatches().add(savedMatch);
        addParticipant(id, userService.findByUsername(savedMatch.getLocal().getUsername()));
        addParticipant(id, userService.findByUsername(savedMatch.getVisitor().getUsername()));
        repo.save(saved);
    }

    @Transactional
    public void addParticipant(long id, User user) {
        Tournament saved = repo.findById(id).orElseThrow();
        saved.getParticipants().add(user);
        repo.save(saved);
    }

    @Transactional
    public TournamentDTO modify(long id, TournamentDTO dto) {
        Tournament saved = repo.findById(id).orElseThrow();
        saved.setName(dto.name());
        saved.setPrice(dto.price());
        saved.setInitDate(dto.initDate());
        saved.setEndDate(dto.endDate());
        return tournamentMapper.toDto(repo.save(saved));
    }

    @Transactional
    public void delete(long id) {
        Tournament saved = repo.findById(id).orElse(null);
        if (saved != null) {
            saved.getParticipants().clear();
            saved.getMatches().clear();
            repo.delete(saved);
        }
    }
}

