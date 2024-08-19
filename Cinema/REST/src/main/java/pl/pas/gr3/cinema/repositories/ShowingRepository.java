package pl.pas.gr3.cinema.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.pas.gr3.cinema.aspects.logging.LoggerInterceptor;
import pl.pas.gr3.cinema.model.Showing;

import java.util.UUID;

@Repository
@LoggerInterceptor
@Transactional(propagation = Propagation.MANDATORY)
public interface ShowingRepository extends JpaRepository<Showing, UUID> {}
