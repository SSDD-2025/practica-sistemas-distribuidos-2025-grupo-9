package es.urjc.club_tenis.controller.rest;

import es.urjc.club_tenis.dto.court.CourtDTO;
import es.urjc.club_tenis.dto.match.MatchDTO;
import es.urjc.club_tenis.dto.tournament.TournamentDTO;
import es.urjc.club_tenis.service.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.media.*;

import java.net.URI;
import java.util.Collection;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/api")
public class TournamentRestController {

    @Autowired
    private TournamentService tournamentService;

    @Operation(summary = "Get all Tournaments")
    @ApiResponses(value = {
            @ApiResponse
                    (
                            responseCode = "200",
                            description = "Returns all Tournaments. If there are none, it returns an empty Collection.",
                            content = {@Content
                                    (
                                            mediaType = "application/json",
                                            array = @ArraySchema(schema = @Schema(implementation= TournamentDTO.class))
                                    )}
                    ),
    })
    @GetMapping("/tournaments")
    public Collection<TournamentDTO> getTournamentCourts(@RequestParam(defaultValue = "1") int page){
        return tournamentService.findAll(page).toList();
    }

    @Operation(summary = "Get Tournament by ID")
    @ApiResponses(value = {
            @ApiResponse
                    (
                            responseCode = "200",
                            description = "Found the Tournament",
                            content = {@Content
                                    (
                                            mediaType = "application/json",
                                            schema = @Schema(implementation=TournamentDTO.class)
                                    )}
                    ),
            @ApiResponse
                    (
                            responseCode = "404",
                            description = "Tournament not found",
                            content = @Content
                    )
    })
    @GetMapping("/tournament/{id}")
    public TournamentDTO getTournament(@PathVariable long id) {
        return tournamentService.findById(id);
    }

    @Operation(summary = "Create Tournament")
    @ApiResponses(value = {
            @ApiResponse
                    (
                            responseCode = "201",
                            description = "Tournament Created",
                            content = {@Content
                                    (
                                            mediaType = "application/json",
                                            schema = @Schema(implementation=TournamentDTO.class)
                                    )}
                    ),
            @ApiResponse
                    (
                            responseCode = "401",
                            description = "UnAuthorised",
                            content = @Content
                    ),
            @ApiResponse
                    (
                            responseCode = "500",
                            description = "Wrong Parameters",
                            content = @Content
                    )
    })
    @PostMapping("/tournament/")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<TournamentDTO> createTournament(@RequestBody TournamentDTO tournament) {
        TournamentDTO created = tournamentService.save(tournament);
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(summary = "Update Tournament")
    @ApiResponses(value = {
            @ApiResponse
                    (
                            responseCode = "201",
                            description = "Tournament Updated",
                            content = {@Content
                                    (
                                            mediaType = "application/json",
                                            schema = @Schema(implementation=TournamentDTO.class)
                                    )}
                    ),
            @ApiResponse
                    (
                            responseCode = "401",
                            description = "UnAuthorised",
                            content = @Content
                    ),
            @ApiResponse
                    (
                            responseCode = "404",
                            description = "Tournament Not Found",
                            content = @Content
                    ),
            @ApiResponse
                    (
                            responseCode = "500",
                            description = "Wrong Parameters",
                            content = @Content
                    )
    })
    @PutMapping("/tournament/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public TournamentDTO updateTournament(@PathVariable long id, @RequestBody TournamentDTO tournament) {
        return tournamentService.modify(id, tournament);
    }

    @Operation(summary = "Delete Tournament")
    @ApiResponses(value = {
            @ApiResponse
                    (
                            responseCode = "200",
                            description = "Tournament Deleted",
                            content = @Content
                    ),
            @ApiResponse
                    (
                            responseCode = "401",
                            description = "UnAuthorised",
                            content = @Content
                    ),
            @ApiResponse
                    (
                            responseCode = "404",
                            description = "Tournament Not Found",
                            content = @Content
                    )
    })
    @DeleteMapping("/tournament/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public void deleteTournament(@PathVariable long id) {
        tournamentService.delete(id);
    }

    @Operation(summary = "Add a Match to a Tournament")
    @ApiResponses(value = {
            @ApiResponse
                    (
                            responseCode = "200",
                            description = "Match added",
                            content = @Content
                    ),
            @ApiResponse
                    (
                            responseCode = "401",
                            description = "UnAuthorised",
                            content = @Content
                    ),
            @ApiResponse
                    (
                            responseCode = "404",
                            description = "Tournament Not Found",
                            content = @Content
                    ),
            @ApiResponse
                    (
                            responseCode = "500",
                            description = "Wrong Parameters",
                            content = @Content
                    )
    })
    @PostMapping("/tournament/{id}/addMatch")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public void addMatch(@PathVariable long id, @RequestBody MatchDTO match) {
        tournamentService.addMatch(id, match);
    }
}
