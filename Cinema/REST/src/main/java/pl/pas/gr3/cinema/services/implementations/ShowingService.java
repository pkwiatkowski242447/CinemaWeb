package pl.pas.gr3.cinema.services.implementations;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.pas.gr3.cinema.aspects.logging.LoggerInterceptor;
import pl.pas.gr3.cinema.repositories.MovieRepository;
import pl.pas.gr3.cinema.repositories.ShowingRepository;

@Service
@RequiredArgsConstructor
@LoggerInterceptor
public class ShowingService {

    private final ShowingRepository showingRepository;
    private final MovieRepository movieRepository;
}
