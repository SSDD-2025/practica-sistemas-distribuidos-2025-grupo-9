package es.urjc.club_tenis.controller.rest;

import es.urjc.club_tenis.model.TennisMatch;
import es.urjc.club_tenis.repositories.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.*;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/matches")
public class MatchRestController {

    @Autowired
    private MatchRepository matchRepository;

    @GetMapping("/")
    public Collection<TennisMatch> getMatches(){
        return matchRepository.findAll();
    }

    @GetMapping("/{id}")
    public TennisMatch getMatch(@PathVariable long id){
        return matchRepository.findById(id).orElseThrow();
    }

    @PostMapping("/")
    public ResponseEntity<TennisMatch> createMatch(@RequestBody TennisMatch match){
        matchRepository.save(match);
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(match.getId()).toUri();

        return ResponseEntity.created(location).body(match);
    }

    @DeleteMapping("/{id}")
    public TennisMatch deleteMatch(@PathVariable long id){
        TennisMatch match = matchRepository.findById(id).orElseThrow();

        matchRepository.deleteById(id);

        return match;
    }

    @PutMapping("/{id}")
    public TennisMatch replaceMatch(@PathVariable long id, @RequestBody TennisMatch match){
        if(matchRepository.existsById(id)){
            match.setId(id);
            matchRepository.save(match);
            return match;
        }
        else throw new NoSuchElementException();
    }

}
