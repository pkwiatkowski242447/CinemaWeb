package mapping.docs;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.pas.gr3.cinema.mapping.docs.MovieDoc;

import java.util.UUID;

import static org.bson.assertions.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class MovieDocTest {

    private static UUID uuidNo1;
    private static UUID uuidNo2;

    private static String movieTitleNo1;
    private static String movieTitleNo2;

    private static double movieBasePriceNo1;
    private static double movieBasePriceNo2;

    private static int scrRoomNumberNo1;
    private static int scrRoomNumberNo2;

    private static int numberOfAvailableSeatsNo1;
    private static int numberOfAvailableSeatsNo2;

    private MovieDoc movieDocNo1;
    private MovieDoc movieDocNo2;

    @BeforeAll
    public static void initializeVariables() {
        uuidNo1 = UUID.randomUUID();
        uuidNo2 = UUID.randomUUID();
        movieTitleNo1 = "ExampleMovieTitleNo1";
        movieTitleNo2 = "ExampleMovieTitleNo2";
        movieBasePriceNo1 = 40.00;
        movieBasePriceNo2 = 50.00;
        scrRoomNumberNo1 = 10;
        scrRoomNumberNo2 = 20;
        numberOfAvailableSeatsNo1 = 45;
        numberOfAvailableSeatsNo2 = 60;
    }

    @BeforeEach
    public void initializeMovieDocs() {
        movieDocNo1 = new MovieDoc(uuidNo1, movieTitleNo1, movieBasePriceNo1, scrRoomNumberNo1, numberOfAvailableSeatsNo1);
        movieDocNo2 = new MovieDoc(uuidNo2, movieTitleNo2, movieBasePriceNo2, scrRoomNumberNo2, numberOfAvailableSeatsNo2);
    }

    @Test
    public void movieDocNoArgsConstructorTest() {
        MovieDoc movieDoc = new MovieDoc();
        assertNotNull(movieDoc);
    }

    @Test
    public void movieDocAllArgsConstructorAndGettersTest() {
        MovieDoc movieDoc = new MovieDoc(uuidNo1, movieTitleNo1, movieBasePriceNo1, scrRoomNumberNo1, numberOfAvailableSeatsNo1);
        assertNotNull(movieDoc);
        assertEquals(uuidNo1, movieDoc.getMovieID());
        assertEquals(movieTitleNo1, movieDoc.getMovieTitle());
        assertEquals(movieBasePriceNo1, movieDoc.getMovieBasePrice());
        assertEquals(scrRoomNumberNo1, movieDoc.getScrRoomNumber());
        assertEquals(numberOfAvailableSeatsNo1, movieDoc.getNumberOfAvailableSeats());
    }

    @Test
    public void movieDocMovieIdSetterTest() {
        UUID movieDocIdBefore = movieDocNo1.getMovieID();
        assertNotNull(movieDocIdBefore);
        UUID newMovieDocId = UUID.randomUUID();
        assertNotNull(newMovieDocId);
        movieDocNo1.setMovieID(newMovieDocId);
        UUID movieDocIdAfter = movieDocNo1.getMovieID();
        assertNotNull(movieDocIdAfter);
        assertEquals(newMovieDocId, movieDocIdAfter);
        assertNotEquals(movieDocIdBefore, movieDocIdAfter);
    }

    @Test
    public void movieDocMovieTitleSetterTest() {
        String movieDocTitleBefore = movieDocNo1.getMovieTitle();
        assertNotNull(movieDocTitleBefore);
        String newMovieDocTitle = "NewMovieTitle";
        assertNotNull(newMovieDocTitle);
        movieDocNo1.setMovieTitle(newMovieDocTitle);
        String movieDocTitleAfter = movieDocNo1.getMovieTitle();
        assertNotNull(movieDocTitleAfter);
        assertEquals(newMovieDocTitle, movieDocTitleAfter);
        assertNotEquals(movieDocTitleBefore, movieDocTitleAfter);
    }

    @Test
    public void movieDocMovieBasePriceSetterTest() {
        double movieDocBasePriceBefore = movieDocNo1.getMovieBasePrice();
        double newMovieDocBasePrice = 45.75;
        movieDocNo1.setMovieBasePrice(newMovieDocBasePrice);
        double movieDocBasePriceAfter = movieDocNo1.getMovieBasePrice();
        assertEquals(newMovieDocBasePrice, movieDocBasePriceAfter);
        assertNotEquals(movieDocBasePriceBefore, movieDocBasePriceAfter);
    }

    @Test
    public void movieDocMovieScrRoomSetterTest() {
        int movieDocScrRoomBefore = movieDocNo2.getScrRoomNumber();
        int newMovieDocScrRoom = 23;
        movieDocNo2.setScrRoomNumber(newMovieDocScrRoom);
        int movieDocScrRoomAfter = movieDocNo2.getScrRoomNumber();
        assertEquals(newMovieDocScrRoom, movieDocScrRoomAfter);
        assertNotEquals(movieDocScrRoomBefore, movieDocScrRoomAfter);
    }

    @Test
    public void movieDocMovieNumberOfAvailableSeatsSetterTest() {
        int movieDocNumberOfAvailableSeatsBefore = movieDocNo1.getNumberOfAvailableSeats();
        int newMovieDocNumberOfAvailableSeats = 150;
        movieDocNo1.setNumberOfAvailableSeats(newMovieDocNumberOfAvailableSeats);
        int movieDocNumberOfAvailableSeatsAfter = movieDocNo1.getNumberOfAvailableSeats();
        assertEquals(newMovieDocNumberOfAvailableSeats, movieDocNumberOfAvailableSeatsAfter);
        assertNotEquals(movieDocNumberOfAvailableSeatsBefore, movieDocNumberOfAvailableSeatsAfter);
    }
}
