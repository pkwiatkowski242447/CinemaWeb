package pl.pas.gr3.mvc.dao.interfaces;

import pl.pas.gr3.dto.output.MovieDTO;
import pl.pas.gr3.dto.input.MovieInputDTO;
import pl.pas.gr3.mvc.exceptions.daos.movie.MovieDaoCreateException;
import pl.pas.gr3.mvc.exceptions.daos.movie.MovieDaoDeleteException;
import pl.pas.gr3.mvc.exceptions.daos.movie.MovieDaoReadException;
import pl.pas.gr3.mvc.exceptions.daos.movie.MovieDaoUpdateException;

import java.util.List;

public interface IMovieDao {

    // Create methods

    MovieDTO create(MovieInputDTO movieInputDTO) throws MovieDaoCreateException;

    // Read methods

    MovieDTO readMovieForChange(MovieDTO movieDTO) throws MovieDaoReadException;
    List<MovieDTO> findAll() throws MovieDaoReadException;

    // Update methods

    void updateMovie(MovieDTO movieDTO) throws MovieDaoUpdateException;

    // Delete methods

    void deleteMovie(MovieDTO movieDTO) throws MovieDaoDeleteException;
}
