package pl.pas.gr3.cinema.services.implementations;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.pas.gr3.cinema.aspects.logging.LoggerInterceptor;
import pl.pas.gr3.cinema.repositories.MovieRepository;

@Service
@LoggerInterceptor
@RequiredArgsConstructor
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class MovieService {

    private final MovieRepository movieRepository;
}
