package mapping.mappers;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.pas.gr3.cinema.mapping.docs.MovieDoc;
import pl.pas.gr3.cinema.mapping.mappers.MovieMapper;
import pl.pas.gr3.cinema.model.Movie;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MovieMapperTest {

    private static UUID uuidNo1;
    private static String movieTitleNo1;
    private static double movieBasePriceNo1;
    private static int scrRoomNumberNo1;
    private static int numberOfAvailableSeatsNo1;

    private MovieDoc movieDocNo1;
    private Movie movieNo1;

    @BeforeAll
    public static void initializeVariables() {
        uuidNo1 = UUID.randomUUID();
        movieTitleNo1 = "MovieExampleTitleNo1";
        movieBasePriceNo1 = 45.75;
        scrRoomNumberNo1 = 4;
        numberOfAvailableSeatsNo1 = 75;
    }

    @BeforeEach
    public void initializeMovieDocsAndMovies() {
        movieDocNo1 = new MovieDoc(uuidNo1, movieTitleNo1, movieBasePriceNo1, scrRoomNumberNo1, numberOfAvailableSeatsNo1);
        movieNo1 = new Movie(uuidNo1, movieTitleNo1, movieBasePriceNo1, scrRoomNumberNo1, numberOfAvailableSeatsNo1);
    }

    @SuppressWarnings("InstantiationOfUtilityClass")
    @Test
    public void movieMapperConstructorTest() {
        MovieMapper movieMapper = new MovieMapper();
        assertNotNull(movieMapper);
    }

    @Test
    public void movieMapperMovieToMovieDocTestPositive() {
        MovieDoc movieDoc = MovieMapper.toMovieDoc(movieNo1);
        assertNotNull(movieDoc);
        assertEquals(movieNo1.getMovieID(), movieDoc.getMovieID());
        assertEquals(movieNo1.getMovieTitle(), movieDoc.getMovieTitle());
        assertEquals(movieNo1.getMovieBasePrice(), movieDoc.getMovieBasePrice());
        assertEquals(movieNo1.getScrRoomNumber(), movieDoc.getScrRoomNumber());
        assertEquals(movieNo1.getNumberOfAvailableSeats(), movieDoc.getNumberOfAvailableSeats());
    }

    @Test
    public void movieMapperMovieDocToMovieTestPositive() {
        Movie movie = MovieMapper.toMovie(movieDocNo1);
        assertNotNull(movie);
        assertEquals(movieDocNo1.getMovieID(), movie.getMovieID());
        assertEquals(movieDocNo1.getMovieTitle(), movie.getMovieTitle());
        assertEquals(movieDocNo1.getMovieBasePrice(), movie.getMovieBasePrice());
        assertEquals(movieDocNo1.getScrRoomNumber(), movie.getScrRoomNumber());
        assertEquals(movieDocNo1.getNumberOfAvailableSeats(), movie.getNumberOfAvailableSeats());
    }
}
