package pl.pas.gr3.cinema.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.pas.gr3.cinema.aspects.logging.LoggerInterceptor;
import pl.pas.gr3.cinema.model.Movie;

import java.util.UUID;

@Repository
@LoggerInterceptor
public interface MovieRepository extends JpaRepository<Movie, UUID> {}
