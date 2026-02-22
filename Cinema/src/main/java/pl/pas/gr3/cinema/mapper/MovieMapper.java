package pl.pas.gr3.cinema.mapper;

import org.mapstruct.Mapper;
import pl.pas.gr3.cinema.dto.movie.MovieResponse;
import pl.pas.gr3.cinema.entity.Movie;

@Mapper(componentModel = "spring")
public interface MovieMapper {

    MovieResponse toResponse(Movie movie);
}
