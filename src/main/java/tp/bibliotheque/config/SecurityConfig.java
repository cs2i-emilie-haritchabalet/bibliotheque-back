package tp.bibliotheque.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import tp.bibliotheque.entity.Utilisateur;
import tp.bibliotheque.repository.UtilisateurRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UtilisateurRepository utilisateurRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**", "/h2-console/**").permitAll()

                        // Administration des utilisateurs
                        .requestMatchers(HttpMethod.POST, "/api/utilisateurs").hasRole("BIBLIOTHECAIRE")
                        .requestMatchers(HttpMethod.GET, "/api/utilisateurs").hasRole("BIBLIOTHECAIRE")
                        .requestMatchers(HttpMethod.PATCH, "/api/utilisateurs/*/credit-caution").hasRole("BIBLIOTHECAIRE")
                        .requestMatchers(HttpMethod.PATCH, "/api/utilisateurs/*/debit-caution").hasRole("BIBLIOTHECAIRE")

                        // Administration des ressources
                        .requestMatchers(HttpMethod.POST, "/api/ressources").hasRole("BIBLIOTHECAIRE")

                        // Gestion des retards et relances
                        .requestMatchers("/api/retards/**").hasRole("BIBLIOTHECAIRE")

                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // Toutes les autres routes nécessitent une authentification
                        .anyRequest().authenticated()
                )
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .httpBasic(Customizer.withDefaults())
                .build();
    }

    @Bean
    @org.springframework.context.annotation.Profile("!test")
    public UserDetailsService userDetailsService() {
        return username -> {
            Utilisateur utilisateur = utilisateurRepository.findByEmail(username)
                    .orElseThrow(() -> new org.springframework.security.core.userdetails.UsernameNotFoundException(username));

            UserDetails user = User.withUsername(utilisateur.getEmail())
                    .password(utilisateur.getMotDePasse())
                    .roles(utilisateur.getRole().name())
                    .disabled(!Boolean.TRUE.equals(utilisateur.getActif()))
                    .build();

            return user;
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
