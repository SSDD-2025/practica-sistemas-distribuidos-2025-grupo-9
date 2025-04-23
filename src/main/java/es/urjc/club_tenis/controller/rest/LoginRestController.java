package es.urjc.club_tenis.controller.rest;


import es.urjc.club_tenis.dto.court.CourtDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.media.*;

import es.urjc.club_tenis.security.jwt.*;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
public class LoginRestController {

    @Autowired
    private UserLoginService userService;

    @Operation(summary = "Login")
    @ApiResponses(value = {
            @ApiResponse
                    (
                            responseCode = "200",
                            description = "Authentication successful",
                            content = @Content
                    ),
            @ApiResponse
                    (
                            responseCode = "401",
                            description = "Wrong username or password",
                            content = @Content
                    ),
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest loginRequest,
            HttpServletResponse response) {

        return userService.login(response, loginRequest);
    }

    @Operation(summary = "Refresh Authentication Token")
    @ApiResponses(value = {
            @ApiResponse
                    (
                            responseCode = "200",
                            description = "Authentication successful",
                            content = @Content
                    ),
            @ApiResponse
                    (
                            responseCode = "200",
                            description = "Failure processing refresh token.(When trying to refresh while being logged out)",
                            content = @Content
                    ),
    })
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(
            @CookieValue(name = "RefreshToken", required = false) String refreshToken, HttpServletResponse response) {

        return userService.refresh(response, refreshToken);
    }

    @Operation(summary = "Logout")
    @ApiResponses(value = {
            @ApiResponse
                    (
                            responseCode = "200",
                            description = "Logout successful",
                            content = @Content
                    ),
    })
    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logOut(HttpServletResponse response) {
        return ResponseEntity.ok(new AuthResponse(AuthResponse.Status.SUCCESS, userService.logout(response)));
    }
}
