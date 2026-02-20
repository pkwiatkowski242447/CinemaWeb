package pl.pas.gr3.cinema.mapper;

import org.mapstruct.Mapper;
import pl.pas.gr3.cinema.dto.output.MovieResponse;
import pl.pas.gr3.cinema.entity.Movie;

@Mapper
public interface MovieMapper {

    MovieResponse toResponse(Movie movie);
}
