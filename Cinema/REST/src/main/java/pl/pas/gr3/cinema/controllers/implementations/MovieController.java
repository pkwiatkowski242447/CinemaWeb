package pl.pas.gr3.cinema.controllers.implementations;

import org.springframework.web.bind.annotation.RestController;
import pl.pas.gr3.cinema.aspects.logging.LoggerInterceptor;
import pl.pas.gr3.cinema.controllers.interfaces.IMovieController;

@RestController
@LoggerInterceptor
public class MovieController implements IMovieController {
}
