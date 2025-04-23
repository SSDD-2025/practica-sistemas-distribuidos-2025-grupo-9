package es.urjc.club_tenis.security;

import es.urjc.club_tenis.security.jwt.JwtRequestFilter;
import es.urjc.club_tenis.security.jwt.UnauthorizedHandlerJwt;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;


import java.util.logging.Logger;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfiguration {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    Logger logger = Logger.getLogger("es.urjc.club_tenis.controller");

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private UnauthorizedHandlerJwt unauthorizedHandlerJwt;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authCinfig) throws Exception {
        return authCinfig.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    @Order(2)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        RequestMatcher apiUrls = (antMatcher("/api/**"));

        NegatedRequestMatcher nonApiUrls = new NegatedRequestMatcher(apiUrls);

        http
                .securityMatcher(nonApiUrls)
                .csrf().disable()
                .authenticationProvider(authenticationProvider())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/", "/signin", "/courts", "/profile/**", "/tournaments", "/tournament/{id}", "/matches", "/match/{id}", "/profile-picture/**", "/css/**", "/ball.svg", "/favicon.ico", "/error/**", "/style.css")
                            .permitAll()
                        .requestMatchers("/match/new", "/match/*/update", "/court/**", "/match")
                            .hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/match","/match/*/update","/match/**")
                            .hasAnyRole("USER")
                        .requestMatchers("/users", "/users/delete/**", "/tournament/new", "/tournament/*/modify", "/tournament/*/addMatch", "/court/*/modify", "/court/*/delete", "/court/new")
                            .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/users/delete-confirmation/**")
                            .hasRole("ADMIN")
                )

                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .failureUrl("/login?error")
                        .defaultSuccessUrl("/")
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll())
                .exceptionHandling(ex -> {
                    logger.warning(ex.toString());
                    ex.accessDeniedPage("/error/accessDenied/web");
                });

        return http.build();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
                .authenticationProvider(authenticationProvider())
                .securityMatcher("/api/**")
                .exceptionHandling(handling -> handling.authenticationEntryPoint(unauthorizedHandlerJwt))
                .authorizeHttpRequests(authorize -> authorize
                        // PRIVATE ENDPOINTS
                        .requestMatchers(HttpMethod.POST, "/api/matches").hasAnyRole("USER")
                        .requestMatchers(HttpMethod.PUT, "/api/match/**").hasAnyRole("USER")
                        .requestMatchers(HttpMethod.DELETE, "/api/match/**").hasAnyRole("USER")
                        .requestMatchers(HttpMethod.POST, "/api/tournaments").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/tournament/**").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/tournament/**").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/courts").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/court/**").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/court/**").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/user/**").hasAnyRole("USERS")
                        .requestMatchers(HttpMethod.PUT, "/api/user/**").hasAnyRole("USERS")
                        // PUBLIC ENDPOINTS
                        .requestMatchers("/api/**").permitAll()
                )
                .formLogin(formLogin -> formLogin.disable())
                .csrf(csrf -> csrf.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}
