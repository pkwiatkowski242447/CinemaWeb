package pl.pas.gr3.cinema.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.pas.gr3.cinema.exceptions.account.AccountNotFoundException;
import pl.pas.gr3.cinema.model.users.AccessLevel;
import pl.pas.gr3.cinema.model.users.Account;
import pl.pas.gr3.cinema.repositories.AccountRepository;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@PropertySource(value = {
        "classpath:properties/consts.properties",
        "classpath:properties/key.properties",
        "classpath:properties/urls.properties"
})
public class ApplicationConfig {

    private final AccountRepository accountRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            Account account = accountRepository.findByLogin(username)
                    .orElse(null);

            if (account == null) return null;

            List<GrantedAuthority> listOfAuthorities = new ArrayList<>();
            for (AccessLevel accessLevel : account.getAccessLevels()) {
                listOfAuthorities.add(new SimpleGrantedAuthority("ROLE_" + accessLevel.getClass().getSimpleName().toUpperCase()));
            }

            return new org.springframework.security.core.userdetails.User(account.getLogin(),
                    account.getPassword(),
                    account.getActive(),
                    true,
                    true,
                    !account.getBlocked(),
                    listOfAuthorities);
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
