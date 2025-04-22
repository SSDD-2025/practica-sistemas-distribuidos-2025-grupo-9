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

    @GetMapping("/courts")
    public Collection<CourtDTO> getPageCourts(@RequestParam(defaultValue = "0") int page){
        return courtService.findAll(page).toList();
    }

    @GetMapping("/court/{id}")
    public ResponseEntity<CourtDTO> getCourt(@PathVariable long id) {
        CourtDTO court = courtService.findById(id);
        if (court == null){
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>(court, HttpStatus.OK);
    }

    @PostMapping("/court")
    public ResponseEntity<CourtDTO> createCourt(Court court){
        CourtDTO savedCourt = courtService.save(court);
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(savedCourt.id()).toUri();
        return ResponseEntity.created(location).body(savedCourt);
    }

    @PutMapping("/court/{id}")
    public CourtDTO updateCourt(@PathVariable long id, Court court){
        courtService.findById(id);
        court.setId(id);
        return courtService.update(court);
    }

    @DeleteMapping("/court/{id}")
    @Transactional
    public CourtDTO deleteCourt(@PathVariable long id){
        CourtDTO deletedCourt = courtService.findById(id);
        courtService.delete(deletedCourt.id());
        return deletedCourt;
    }
}