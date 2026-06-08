package tp.bibliotheque.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@Profile("test")
public class TestSecurityConfig {

    @Bean
    public UserDetailsService testUserDetailsService(PasswordEncoder encoder) {

        UserDetails admin = User.withUsername("admin")
                .password(encoder.encode("admin123"))
                .roles("BIBLIOTHECAIRE")
                .build();

        UserDetails alice = User.withUsername("alice")
                .password(encoder.encode("alice123"))
                .roles("ETUDIANT")
                .build();

        return new InMemoryUserDetailsManager(admin, alice);
    }
}
