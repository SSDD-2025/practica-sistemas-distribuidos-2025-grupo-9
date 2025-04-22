package es.urjc.club_tenis.controller.rest;

import es.urjc.club_tenis.dto.match.MatchDTO;
import es.urjc.club_tenis.dto.tournament.TournamentDTO;
import es.urjc.club_tenis.service.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Collection;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/api")
public class TournamentRestController {

    @Autowired
    private TournamentService tournamentService;

    @GetMapping("/tournaments/")
    public Collection<TournamentDTO> getAllTournaments() {
        return tournamentService.findAll();
    }

    @GetMapping("/tournament/{id}")
    public TournamentDTO getTournament(@PathVariable long id) {
        return tournamentService.findById(id);
    }

    @PostMapping("/tournament/")
    public ResponseEntity<TournamentDTO> createTournament(@RequestBody TournamentDTO tournament) {
        TournamentDTO created = tournamentService.save(tournament);
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/tournament/{id}")
    public TournamentDTO updateTournament(@PathVariable long id, @RequestBody TournamentDTO tournament) {
        return tournamentService.modify(id, tournament);
    }

    @DeleteMapping("/tournament/{id}")
    public void deleteTournament(@PathVariable long id) {
        tournamentService.delete(id);
    }

    @PostMapping("/tournament/{id}/addMatch")
    public void addMatch(@PathVariable long id, @RequestBody MatchDTO match) {
        tournamentService.addMatch(id, match);
    }
}
