package es.urjc.club_tenis.controller.rest;

import es.urjc.club_tenis.dto.match.MatchDTO;
import es.urjc.club_tenis.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.*;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/api")
public class MatchRestController {

    @Autowired
    private MatchService matchService;

    @GetMapping("/matches")
    public Collection<MatchDTO> getMatches(@RequestParam(defaultValue = "1") int page){
        return matchService.findAll(page).toList();
    }

    @GetMapping("/match/{id}")
    public MatchDTO getMatch(@PathVariable long id){
        return matchService.findById(id);
    }

    @PostMapping("/match")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<MatchDTO> createMatch(@RequestBody MatchDTO match){
        match = matchService.save(match);
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(match.id()).toUri();

        return ResponseEntity.created(location).body(match);
    }

    @DeleteMapping("/match/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public MatchDTO deleteMatch(@PathVariable long id){
        return matchService.delete(id);
    }

    @PutMapping("/match/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public MatchDTO replaceMatch(@PathVariable long id, @RequestBody MatchDTO match){
        return matchService.modify(id, match);
    }

}
