package pl.pas.gr3.cinema.services.implementations;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.pas.gr3.cinema.aspects.logging.LoggerInterceptor;
import pl.pas.gr3.cinema.repositories.MovieRepository;

@Service
@RequiredArgsConstructor
@LoggerInterceptor
public class MovieService {

    private final MovieRepository movieRepository;
}
