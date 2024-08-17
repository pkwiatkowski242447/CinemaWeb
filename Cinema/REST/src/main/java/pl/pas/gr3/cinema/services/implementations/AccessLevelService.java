package pl.pas.gr3.cinema.services.implementations;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.pas.gr3.cinema.aspects.logging.LoggerInterceptor;
import pl.pas.gr3.cinema.repositories.AccessLevelRepository;
import pl.pas.gr3.cinema.repositories.AccountRepository;

@Service
@RequiredArgsConstructor
@LoggerInterceptor
public class AccessLevelService {

    private AccessLevelRepository accessLevelRepository;
    private AccountRepository accountRepository;
}
