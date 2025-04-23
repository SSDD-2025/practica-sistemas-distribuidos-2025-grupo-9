package es.urjc.club_tenis.controller.rest;

import es.urjc.club_tenis.dto.court.CourtDTO;
import es.urjc.club_tenis.dto.court.CourtMapper;
import es.urjc.club_tenis.model.Court;
import es.urjc.club_tenis.service.CourtService;
import io.swagger.v3.oas.annotations.media.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.*;

import java.util.Collection;

@RestController
@RequestMapping("/api")
public class CourtRestController {

    @Autowired
    private CourtService courtService;

    @Autowired
    private CourtMapper courtMapper;

    @Operation(summary = "Get all Courts")
    @ApiResponses(value = {
            @ApiResponse
                    (
                            responseCode = "200",
                            description = "Returns all Courts. If there are none, it returns an empty Collection.",
                            content = {@Content
                                    (
                                            mediaType = "application/json",
                                            array = @ArraySchema(schema = @Schema(implementation=CourtDTO.class))
                                    )}
                    ),
    })
    @GetMapping("/courts")
    public Collection<CourtDTO> getPageCourts(@RequestParam(defaultValue = "1") int page){
        return courtService.findAll(page).toList();
    }

    @Operation(summary = "Get Court by ID")
    @ApiResponses(value = {
            @ApiResponse
                    (
                            responseCode = "200",
                            description = "Found the Court",
                            content = {@Content
                                    (
                                            mediaType = "application/json",
                                            schema = @Schema(implementation=CourtDTO.class)
                                    )}
                    ),
            @ApiResponse
                    (
                            responseCode = "404",
                            description = "Court not found",
                            content = @Content
                    )
    })
    @GetMapping("/court/{id}")
    public ResponseEntity<CourtDTO> getCourt(@PathVariable long id) {
        CourtDTO court = courtService.findById(id);
        if (court == null){
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>(court, HttpStatus.OK);
    }

    @Operation(summary = "Create Court")
    @ApiResponses(value = {
            @ApiResponse
                    (
                            responseCode = "201",
                            description = "Court Created",
                            content = {@Content
                                    (
                                            mediaType = "application/json",
                                            schema = @Schema(implementation=CourtDTO.class)
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
    @PostMapping("/court")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public CourtDTO createCourt(Court court){
        return courtService.save(court);
    }

    @Operation(summary = "Update Court")
    @ApiResponses(value = {
            @ApiResponse
                    (
                            responseCode = "201",
                            description = "Court Updated",
                            content = {@Content
                                    (
                                            mediaType = "application/json",
                                            schema = @Schema(implementation=CourtDTO.class)
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
                            description = "Court Not Found",
                            content = @Content
                    ),
            @ApiResponse
                    (
                            responseCode = "500",
                            description = "Wrong Parameters",
                            content = @Content
                    )
    })
    @PutMapping("/court/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourtDTO> updateCourt(@PathVariable long id, Court court){
        CourtDTO saved = courtService.findById(id);
        if (saved == null){
            return ResponseEntity.notFound().build();
        }else{
            court.setId(id);
            return new ResponseEntity<>(courtService.update(court), HttpStatus.OK);
        }
    }

    @Operation(summary = "Delete Court")
    @ApiResponses(value = {
            @ApiResponse
                    (
                            responseCode = "200",
                            description = "Court Deleted",
                            content = {@Content
                                    (
                                            mediaType = "application/json",
                                            schema = @Schema(implementation=CourtDTO.class)
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
                            description = "Court Not Found",
                            content = @Content
                    )
    })
    @DeleteMapping("/court/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<CourtDTO> deleteCourt(@PathVariable long id){
        courtService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}