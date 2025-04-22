package es.urjc.club_tenis.controller.rest;

import es.urjc.club_tenis.dto.court.CourtBasicDTO;
import es.urjc.club_tenis.dto.court.CourtDTO;
import es.urjc.club_tenis.dto.court.CourtMapper;
import es.urjc.club_tenis.model.Court;
import es.urjc.club_tenis.service.CourtService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Collection;
import java.util.List;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/api")
public class CourtRestController {

    @Autowired
    private CourtService courtService;

    @Autowired
    private CourtMapper courtMapper;

    @GetMapping("/courts/")
    public Collection<CourtDTO> getPageCourts(@RequestParam(defaultValue = "1") int page){
        return courtService.findAll(page).toList();
    }

    @GetMapping("/courts/{id}")
    public ResponseEntity<CourtDTO> getCourt(@PathVariable long id) {
        CourtDTO court = courtService.findById(id);
        if (court == null){
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>(court, HttpStatus.OK);
    }

    @PostMapping("/courts/")
    @ResponseStatus(HttpStatus.CREATED)
    public CourtDTO createCourt(Court court){
        return courtService.save(court);
    }

    @PutMapping("/courts/{id}")
    public ResponseEntity<CourtDTO> updateCourt(@PathVariable long id, Court court){
        CourtDTO saved = courtService.findById(id);
        if (saved == null){
            return ResponseEntity.notFound().build();
        }else{
            court.setId(id);
            return new ResponseEntity<>(courtService.update(court), HttpStatus.OK);
        }
    }

    @DeleteMapping("/courts/{id}")
    @Transactional
    public ResponseEntity<CourtDTO> deleteCourt(@PathVariable long id){
        courtService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}