package mapping.docs;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.pas.gr3.cinema.mapping.docs.TicketDoc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.bson.assertions.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TicketDocTest {

    private static UUID uuidNo1;
    private static UUID uuidNo2;

    private static LocalDateTime movieTimeNo1;
    private static LocalDateTime movieTimeNo2;

    private static double ticketFinalPriceNo1;
    private static double ticketFinalPriceNo2;

    private static UUID clientDocNo1;
    private static UUID clientDocNo2;

    private static UUID movieDocNo1;
    private static UUID movieDocNo2;

    private TicketDoc ticketDocNo1;
    private TicketDoc ticketDocNo2;

    @BeforeAll
    public static void initializeVariables() {
        uuidNo1 = UUID.randomUUID();
        uuidNo2 = UUID.randomUUID();
        movieTimeNo1 = LocalDateTime.now().plusDays(7);
        movieTimeNo2 = LocalDateTime.now().plusDays(14);
        ticketFinalPriceNo1 = 50;
        ticketFinalPriceNo2 = 35;
        clientDocNo1 = UUID.randomUUID();
        clientDocNo2 = UUID.randomUUID();
        movieDocNo1 = UUID.randomUUID();
        movieDocNo2 = UUID.randomUUID();
    }

    @BeforeEach
    public void initializeTicketDocs() {
        ticketDocNo1 = new TicketDoc(uuidNo1, movieTimeNo1, ticketFinalPriceNo1, clientDocNo1, movieDocNo1);
        ticketDocNo2 = new TicketDoc(uuidNo2, movieTimeNo2, ticketFinalPriceNo2, clientDocNo2, movieDocNo2);
    }

    @Test
    public void ticketDocNoArgsConstructorTest() {
        TicketDoc ticketDoc = new TicketDoc();
        assertNotNull(ticketDoc);
    }

    @Test
    public void ticketDocAllArgsConstructorAndGettersTest() {
        TicketDoc ticketDoc = new TicketDoc(uuidNo1, movieTimeNo1, ticketFinalPriceNo1, clientDocNo1, movieDocNo1);
        assertNotNull(ticketDoc);
        assertEquals(uuidNo1, ticketDoc.getTicketID());
        assertEquals(movieTimeNo1, ticketDoc.getMovieTime());
        assertEquals(ticketFinalPriceNo1, ticketDoc.getTicketFinalPrice());
        assertEquals(clientDocNo1, ticketDoc.getClientID());
        assertEquals(movieDocNo1, ticketDoc.getMovieID());
    }

    @Test
    public void ticketDocTicketIdSetterTest() {
        UUID ticketDocIdBefore = ticketDocNo1.getTicketID();
        assertNotNull(ticketDocIdBefore);
        UUID newTicketDocId = UUID.randomUUID();
        assertNotNull(newTicketDocId);
        ticketDocNo1.setTicketID(newTicketDocId);
        UUID ticketDocIdAfter = ticketDocNo1.getTicketID();
        assertNotNull(ticketDocIdAfter);
        assertEquals(newTicketDocId, ticketDocIdAfter);
        assertNotEquals(ticketDocIdBefore, ticketDocIdAfter);
    }

    @Test
    public void ticketDocTicketMovieTimeSetterTest() {
        LocalDateTime ticketDocMovieTimeBefore = ticketDocNo1.getMovieTime();
        assertNotNull(ticketDocMovieTimeBefore);
        LocalDateTime newTicketDocMovieTime = LocalDateTime.now().plusDays(8);
        assertNotNull(newTicketDocMovieTime);
        ticketDocNo1.setMovieTime(newTicketDocMovieTime);
        LocalDateTime ticketDocMovieTimeAfter = ticketDocNo1.getMovieTime();
        assertNotNull(ticketDocMovieTimeAfter);
        assertEquals(newTicketDocMovieTime, ticketDocMovieTimeAfter);
        assertNotEquals(ticketDocMovieTimeBefore, ticketDocMovieTimeAfter);
    }

    @Test
    public void ticketDocTicketFinalPriceSetterTest() {
        double ticketDocTicketFinalPriceBefore = ticketDocNo1.getTicketFinalPrice();
        double newTicketDocTicketFinalPrice = 75.50;
        ticketDocNo1.setTicketFinalPrice(newTicketDocTicketFinalPrice);
        double ticketDocTicketFinalPriceAfter = ticketDocNo1.getTicketFinalPrice();
        assertEquals(newTicketDocTicketFinalPrice, ticketDocTicketFinalPriceAfter);
        assertNotEquals(ticketDocTicketFinalPriceBefore, ticketDocTicketFinalPriceAfter);
    }

    @Test
    public void ticketDocTicketClientIdSetterTest() {
        UUID ticketDocClientIdBefore = ticketDocNo1.getClientID();
        assertNotNull(ticketDocClientIdBefore);
        UUID newTicketDocClientId = UUID.randomUUID();
        assertNotNull(newTicketDocClientId);
        ticketDocNo1.setClientID(newTicketDocClientId);
        UUID ticketDocClientIdAfter = ticketDocNo1.getClientID();
        assertNotNull(ticketDocClientIdAfter);
        assertEquals(newTicketDocClientId, ticketDocClientIdAfter);
        assertNotEquals(ticketDocClientIdBefore, ticketDocClientIdAfter);
    }

    @Test
    public void ticketDocTicketMovieIdSetterTest() {
        UUID ticketDocMovieIdBefore = ticketDocNo1.getMovieID();
        assertNotNull(ticketDocMovieIdBefore);
        UUID newTicketDocMovieId = UUID.randomUUID();
        assertNotNull(newTicketDocMovieId);
        ticketDocNo1.setMovieID(newTicketDocMovieId);
        UUID ticketDocMovieIdAfter = ticketDocNo1.getMovieID();
        assertNotNull(ticketDocMovieIdAfter);
        assertEquals(newTicketDocMovieId, ticketDocMovieIdAfter);
        assertNotEquals(ticketDocMovieIdBefore, ticketDocMovieIdAfter);
    }
}
