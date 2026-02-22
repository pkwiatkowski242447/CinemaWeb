package pl.pas.gr3.cinema.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.pas.gr3.cinema.repository.api.MovieRepository;
import pl.pas.gr3.cinema.service.api.MovieService;
import pl.pas.gr3.cinema.entity.Movie;
import pl.pas.gr3.cinema.entity.Ticket;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;

    @Override
    public Movie create(String movieTitle, double movieBasePrice, int scrRoomNumber, int numberOfAvailableSeats) {
        return movieRepository.create(movieTitle, movieBasePrice, scrRoomNumber, numberOfAvailableSeats);
    }

    @Override
    public Movie findByUUID(UUID movieId) {
        return movieRepository.findByUUID(movieId);
    }

    @Override
    public List<Movie> findAll() {
        return movieRepository.findAll();
    }

    @Override
    public void update(Movie movie) {
        movieRepository.update(movie);
    }

    @Override
    public void delete(UUID movieId) {
        movieRepository.delete(movieId);
    }

    @Override
    public List<Ticket> getListOfTicketsForCertainMovie(UUID movieID) {
        return movieRepository.getListOfTicketsForMovie(movieID);
    }
}
