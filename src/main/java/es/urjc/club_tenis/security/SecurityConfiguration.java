package es.urjc.club_tenis.security;

import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.NullRequestCache;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
/*
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authenticationProvider(authenticationProvider());
        http
                .authorizeHttpRequests(authorize -> authorize
        // PUBLIC PAGES
                        .requestMatchers("/", "/signin", "/courts", "/profile/**", "/tournaments", "tournament/", "/login", "/matches").permitAll()
        // PRIVATE PAGES
                        .anyRequest().authenticated())
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .failureUrl("/login?error=true")
                        .defaultSuccessUrl("/")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                );

        //http.csrf(csrf -> csrf.disable());
        return http.build();

    }*/

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authenticationProvider(authenticationProvider())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/", "/signin", "/courts", "/profile/**", "/tournaments",
                                "/tournament/**", "/matches", "/profile-picture/**", "match/**",
                                "/css/**", "/ball.svg","/favicon.ico", "/error/**",
                                "/style.css").permitAll()
                        .requestMatchers("/match/new", "/match/*/update", "/court/**"
                        ).hasAnyRole("USER")
                        .requestMatchers("/users", "/tournament/new", "/tournament/*/modify",
                                "tournament/*/addMatch", "/court/*/modify", "/court/*/delete",
                                "/court/new").hasAnyRole("ADMIN")
                        .anyRequest().authenticated())
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
        http.csrf(csrf -> csrf.disable());
        return http.build();
    }
}
