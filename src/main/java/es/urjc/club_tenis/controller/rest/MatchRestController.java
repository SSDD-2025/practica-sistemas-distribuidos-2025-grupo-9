package es.urjc.club_tenis.controller.rest;

import es.urjc.club_tenis.dto.court.CourtDTO;
import es.urjc.club_tenis.dto.match.MatchDTO;
import es.urjc.club_tenis.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.media.*;

import java.net.URI;
import java.util.*;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/api")
public class MatchRestController {

    @Autowired
    private MatchService matchService;

    @Operation(summary = "Get all Matches")
    @ApiResponses(value = {
            @ApiResponse
                    (
                            responseCode = "200",
                            description = "Returns all Matches. If there are none, it returns an empty Collection.",
                            content = {@Content
                                    (
                                            mediaType = "application/json",
                                            array = @ArraySchema(schema = @Schema(implementation= MatchDTO.class))
                                    )}
                    ),
    })
    @GetMapping("/matches")
    public Collection<MatchDTO> getMatches(@RequestParam(defaultValue = "1") int page){
        return matchService.findAll(page).toList();
    }

    @Operation(summary = "Get Match by ID")
    @ApiResponses(value = {
            @ApiResponse
                    (
                            responseCode = "200",
                            description = "Found the Match",
                            content = {@Content
                                    (
                                            mediaType = "application/json",
                                            schema = @Schema(implementation=MatchDTO.class)
                                    )}
                    ),
            @ApiResponse
                    (
                            responseCode = "404",
                            description = "Match not found",
                            content = @Content
                    )
    })
    @GetMapping("/match/{id}")
    public MatchDTO getMatch(@PathVariable long id){
        return matchService.findById(id);
    }

    @Operation(summary = "Create Match")
    @ApiResponses(value = {
            @ApiResponse
                    (
                            responseCode = "201",
                            description = "Match Created",
                            content = {@Content
                                    (
                                            mediaType = "application/json",
                                            schema = @Schema(implementation=MatchDTO.class)
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
    @PostMapping("/match")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<MatchDTO> createMatch(@RequestBody MatchDTO match){
        match = matchService.save(match);
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(match.id()).toUri();

        return ResponseEntity.created(location).body(match);
    }

    @Operation(summary = "Delete Match")
    @ApiResponses(value = {
            @ApiResponse
                    (
                            responseCode = "200",
                            description = "Match Deleted",
                            content = {@Content
                                    (
                                            mediaType = "application/json",
                                            schema = @Schema(implementation=MatchDTO.class)
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
                            description = "Match Not Found",
                            content = @Content
                    )
    })
    @DeleteMapping("/match/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public MatchDTO deleteMatch(@PathVariable long id){
        return matchService.delete(id);
    }

    @Operation(summary = "Update Match")
    @ApiResponses(value = {
            @ApiResponse
                    (
                            responseCode = "201",
                            description = "Match Updated",
                            content = {@Content
                                    (
                                            mediaType = "application/json",
                                            schema = @Schema(implementation=MatchDTO.class)
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
                            description = "Match Not Found",
                            content = @Content
                    ),
            @ApiResponse
                    (
                            responseCode = "500",
                            description = "Wrong Parameters",
                            content = @Content
                    )
    })
    @PutMapping("/match/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public MatchDTO replaceMatch(@PathVariable long id, @RequestBody MatchDTO match){
        return matchService.modify(id, match);
    }

}
