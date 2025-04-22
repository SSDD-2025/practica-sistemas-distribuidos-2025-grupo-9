package es.urjc.club_tenis.security;

import es.urjc.club_tenis.security.jwt.JwtRequestFilter;
import es.urjc.club_tenis.security.jwt.UnauthorizedHandlerJwt;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
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
import org.springframework.security.web.savedrequest.NullRequestCache;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfiguration {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

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
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http.authenticationProvider(authenticationProvider());
        http
                .securityMatcher("/api/**")
                .exceptionHandling(handling -> handling.authenticationEntryPoint(unauthorizedHandlerJwt));
        http
                .authorizeHttpRequests(authorize -> authorize
                        // PRIVATE ENDPOINTS
                        .requestMatchers(HttpMethod.POST,"api/matches/").hasAnyRole("USER")
                        .requestMatchers(HttpMethod.PUT,"/api/matches/**").hasAnyRole("USER")
                        .requestMatchers(HttpMethod.DELETE,"/api/matches/**").hasAnyRole("USER")
                        .requestMatchers(HttpMethod.POST,"/api/tournaments/").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,"/api/tournaments/**").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE,"/api/tournaments/**").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.POST,"/api/courts/").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,"/api/courts/**").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE,"/api/courts/**").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE,"/api/users/**").hasAnyRole("USERS")
                        .requestMatchers(HttpMethod.PUT,"/api/users/**").hasAnyRole("USERS")
                        // PUBLIC ENDPOINTS
                        .anyRequest().permitAll()
                );
        http.formLogin(formLogin -> formLogin.disable());
        http.csrf(csrf -> csrf.disable());
        http.httpBasic(httpBasic -> httpBasic.disable());
        http.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }


    @Bean
    @Order(2)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authenticationProvider(authenticationProvider())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/", "/signin", "/courts", "/profile/**", "/tournaments",
                                "/tournament/**", "/matches", "/matches/**", "/profile-picture/**", "match/**",
                                "/css/**", "/ball.svg","/favicon.ico", "/error/**",
                                "/style.css").permitAll()
                        .requestMatchers("/match/new", "/match/*/update", "/court/**"
                        ).hasAnyRole("USER")
                        .requestMatchers("/users", "/tournament/new", "/tournament/*/modify",
                                "tournament/*/addMatch", "/court/*/modify", "/court/*/delete",
                                "/court/new").hasAnyRole("ADMIN")
                        .anyRequest().permitAll())
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .failureUrl("/login?error")
                        .defaultSuccessUrl("/")
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                        .invalidSessionUrl("/   ")
                )
                .requestCache(cache -> cache
                        .requestCache(new NullRequestCache())
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/error/accessDenied")
                );
        //http.csrf(csrf -> csrf.disable());

        return http.build();
    }
}
