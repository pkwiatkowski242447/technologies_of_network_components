package pl.tks.gr3.cinema.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.tks.gr3.cinema.adapters.exceptions.crud.user.UserRepositoryReadException;
import pl.tks.gr3.cinema.adapters.model.UserEnt;
import pl.tks.gr3.cinema.adapters.repositories.UserRepository;
import pl.tks.gr3.cinema.security.consts.SecurityMessages;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UserRepository userRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            try {
                UserEnt userEnt = userRepository.findByLogin(username);
                return new org.springframework.security.core.userdetails.User(userEnt.getUserLogin(),
                        userEnt.getUserPassword(),
                        userEnt.isUserStatusActive(),
                        true,
                        true,
                        true,
                        List.of(new SimpleGrantedAuthority("ROLE_" + userEnt.getUserRole().name())));
            } catch (UserRepositoryReadException exception) {
                throw new UsernameNotFoundException(SecurityMessages.USER_NOT_FOUND);
            }
        };
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService());
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
