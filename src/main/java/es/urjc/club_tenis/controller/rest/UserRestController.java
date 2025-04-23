package es.urjc.club_tenis.controller.rest;


import es.urjc.club_tenis.dto.court.CourtDTO;
import es.urjc.club_tenis.dto.match.MatchDTO;
import es.urjc.club_tenis.dto.user.*;
import es.urjc.club_tenis.model.TennisMatch;
import es.urjc.club_tenis.model.User;
import es.urjc.club_tenis.service.MatchService;
import es.urjc.club_tenis.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.media.*;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/api")
public class UserRestController {

    Logger logger = Logger.getLogger("es.urjc.club_tenis.controller");

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserMapper userMapper;

    @Operation(summary = "Get all Users")
    @ApiResponses(value = {
            @ApiResponse
                    (
                            responseCode = "200",
                            description = "Returns all Users. If there are none, it returns an empty Collection.",
                            content = {@Content
                                    (
                                            mediaType = "application/json",
                                            array = @ArraySchema(schema = @Schema(implementation= UserBasicDTO.class))
                                    )}
                    ),
    })
    @GetMapping("/users")
    public List<UserBasicDTO> getPageUsers(@RequestParam(defaultValue = "1") int page){
        return userMapper.toBasicDTOs(userService.findAll(page).toList());
    }

    @Operation(summary = "Get User by Username")
    @ApiResponses(value = {
            @ApiResponse
                    (
                            responseCode = "200",
                            description = "Found the User",
                            content = {@Content
                                    (
                                            mediaType = "application/json",
                                            schema = @Schema(implementation= UserDTO.class)
                                    )}
                    ),
            @ApiResponse
                    (
                            responseCode = "404",
                            description = "User not found",
                            content = @Content
                    )
    })
    @GetMapping("/user/{username}")
    public UserDTO getProfile(@PathVariable String username) {
        User user = userService.findByUsername(username);

        return userMapper.toDTO(user);
    }

    @Operation(summary = "Create User")
    @ApiResponses(value = {
            @ApiResponse
                    (
                            responseCode = "201",
                            description = "User Created",
                            content = {@Content
                                    (
                                            mediaType = "application/json",
                                            schema = @Schema(implementation=UserDTO.class)
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
    @PostMapping("/user")
    public ResponseEntity<UserDTO> createUser(@RequestBody User user){
        user.addRole("USER");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userService.save(user);
        URI location = fromCurrentRequest().path("/{username}").buildAndExpand(savedUser.getUsername()).toUri();
        return ResponseEntity.created(location).body(userMapper.toDTO(savedUser));
    }

    @Operation(summary = "Update User")
    @ApiResponses(value = {
            @ApiResponse
                    (
                            responseCode = "201",
                            description = "User Updated",
                            content = {@Content
                                    (
                                            mediaType = "application/json",
                                            schema = @Schema(implementation=UserDTO.class)
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
                            description = "User Not Found",
                            content = @Content
                    ),
            @ApiResponse
                    (
                            responseCode = "500",
                            description = "Wrong Parameters",
                            content = @Content
                    )
    })
    @PutMapping("/user/{username}")
    @PreAuthorize("#username == principal.username or hasRole('ADMIN')")
    public UserDTO updateUser(@PathVariable String username, @RequestBody User updatedUser){
        User user = userService.findByUsername(username);
        updatedUser.setId(user.getId());
        if(updatedUser.getPassword() != null){
            updatedUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }else{
            updatedUser.setPassword(user.getPassword());
        }

        updatedUser.addRole("USER");
        userService.save(updatedUser);
        return userMapper.toDTO(updatedUser);
    }

    @Operation(summary = "Delete User")
    @ApiResponses(value = {
            @ApiResponse
                    (
                            responseCode = "200",
                            description = "User Deleted",
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
                            description = "User Not Found",
                            content = @Content
                    )
    })
    @DeleteMapping("/user/{username}")
    @PreAuthorize("#username == principal.username or hasRole('ADMIN')")
    @Transactional
    public ResponseEntity<Object> deleteUser(@PathVariable String username){
        String deletedUsername = username;
        userService.delete(username);
        return ResponseEntity.ok().body(deletedUsername + " eliminado");
    }


    @Operation(summary = "Upload profile picture")
    @ApiResponses(value = {
            @ApiResponse
                    (
                            responseCode = "200",
                            description = "Picture added",
                            content = {@Content
                                    (
                                            mediaType = "image/jpeg"
                                    )}
                    ),
            @ApiResponse
                    (
                            responseCode = "400",
                            description = "A picture was not sent to be uploaded",
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
                            description = "User Not Found",
                            content = @Content
                    )
    })
    @PutMapping("/user/{username}/profile-picture")
    @PreAuthorize("#username == principal.username or hasRole('ADMIN')")
    public ResponseEntity<Object> uploadProfilePicture(
            @PathVariable String username,
            @RequestParam MultipartFile profilePicture) throws IOException {

        if (profilePicture.isEmpty()) {
            return ResponseEntity.badRequest().body("No se proporcionó imagen");
        }

        User user = userService.findByUsername(username);
        userService.updateProfilePicture(user, profilePicture);
        byte[] profilePictureNew = profilePicture.getBytes();

        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                .body(profilePictureNew);
    }

    @Operation(summary = "Get profile picture")
    @ApiResponses(value = {
            @ApiResponse
                    (
                            responseCode = "200",
                            description = "Picture added",
                            content = {@Content
                                    (
                                            mediaType = "image/jpeg"
                                    )}
                    ),
            @ApiResponse
                    (
                            responseCode = "400",
                            description = "A picture was not sent to be uploaded",
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
                            description = "User Not Found",
                            content = @Content
                    )
    })
    @GetMapping(value = "/user/{username}/profile-picture")
    public ResponseEntity<Object> getProfilePicture(@PathVariable String username) {

        byte[] profilePicture = userService.getProfilePicture(username);

        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                .body(profilePicture);
    }

}

