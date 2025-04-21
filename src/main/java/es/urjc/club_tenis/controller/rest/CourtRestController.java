package es.urjc.club_tenis.controller.rest;

import es.urjc.club_tenis.dto.court.CourtBasicDTO;
import es.urjc.club_tenis.dto.court.CourtDTO;
import es.urjc.club_tenis.dto.court.CourtMapper;
import es.urjc.club_tenis.model.Court;
import es.urjc.club_tenis.service.CourtService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
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
    public List<CourtBasicDTO> getPageCourts(@RequestParam(defaultValue = "0") int page){
        return courtMapper.toBasicDTOs(courtService.findAll(page).toList());
    }

    @GetMapping("/court/{id}")
    public CourtDTO getCourt(@PathVariable long id) {
        return courtMapper.toDTO(courtService.findById(id));
    }

    @PostMapping("/court")
    public ResponseEntity<Court> createCourt(Court court){
        Court savedCourt = courtService.save(court);
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(savedCourt.getId()).toUri();
        return ResponseEntity.created(location).body(savedCourt);
    }

    @PutMapping("/court/{id}")
    public CourtDTO updateCourt(@PathVariable long id, Court court){
        courtService.findById(id);
        court.setId(id);
        Court updatedCourt = courtService.update(court);
        return courtMapper.toDTO(updatedCourt);
    }

    @DeleteMapping("/court/{id}")
    @Transactional
    public CourtDTO deleteCourt(@PathVariable long id){
        Court deletedCourt = courtService.findById(id);
        courtService.delete(deletedCourt);
        return courtMapper.toDTO(deletedCourt);
    }
}