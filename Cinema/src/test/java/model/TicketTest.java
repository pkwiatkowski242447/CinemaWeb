package model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.pas.gr3.cinema.exceptions.model.MovieNullReferenceException;
import pl.pas.gr3.cinema.exceptions.model.TicketCreateException;
import pl.pas.gr3.cinema.model.Movie;
import pl.pas.gr3.cinema.model.Ticket;
import pl.pas.gr3.cinema.model.TicketType;
import pl.pas.gr3.cinema.model.users.Client;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TicketTest {

    private static UUID ticketIDNo1;
    private static UUID ticketIDNo2;
    private static LocalDateTime movieTimeNo1;
    private static LocalDateTime movieTimeNo2;

    private static double ticketFinalPriceNo1;

    private static Client clientNo1;
    private static Client clientNo2;
    private static Movie movieNo1;
    private static Movie movieNo2;
    private static TicketType ticketTypeNo1;
    private static TicketType ticketTypeNo2;

    private static double movieBasePriceNo1;
    private static double movieBasePriceNo2;

    private static int numberOfAvailableSeatsNo1;
    private static int numberOfAvailableSeatsNo2;

    private Ticket ticketNo1;
    private Ticket ticketNo2;
    private Ticket ticketNo3;

    @BeforeAll
    public static void initializeVariables() {
        ticketIDNo1 = UUID.randomUUID();
        ticketIDNo2 = UUID.randomUUID();
        movieTimeNo1 = LocalDateTime.now().plusDays(3).truncatedTo(ChronoUnit.SECONDS);
        movieTimeNo2 = LocalDateTime.now().plusDays(3).truncatedTo(ChronoUnit.SECONDS);
        ticketFinalPriceNo1 = 50.00;
        clientNo1 = new Client(UUID.randomUUID(), "SomeExampleLogin", "SomeExamplePassword");
        clientNo2 = new Client(UUID.randomUUID(), "SomeExampleLogin", "SomeExamplePassword");
        movieBasePriceNo1 = 45.00;
        movieBasePriceNo2 = 47.50;
        numberOfAvailableSeatsNo1 = 45;
        numberOfAvailableSeatsNo2 = 60;
        movieNo1 = new Movie(UUID.randomUUID(), "SomeExampleTitle", movieBasePriceNo1, 1, numberOfAvailableSeatsNo1);
        movieNo2 = new Movie(UUID.randomUUID(), "SomeExampleTitle", movieBasePriceNo2, 2, numberOfAvailableSeatsNo2);
        ticketTypeNo1 = TicketType.NORMAL;
        ticketTypeNo2 = TicketType.REDUCED;
    }

    @BeforeEach
    public void initializeTickets() throws TicketCreateException  {
        ticketNo1 = new Ticket(ticketIDNo1, movieTimeNo1, clientNo1, movieNo1, TicketType.NORMAL);
        ticketNo2 = new Ticket(ticketIDNo2, movieTimeNo2, clientNo2, movieNo2, TicketType.REDUCED);
        ticketNo3 = new Ticket(ticketNo1.getTicketID(),
                ticketNo1.getMovieTime(),
                ticketNo1.getTicketFinalPrice(),
                ticketNo1.getClient(),
                ticketNo1.getMovie());
    }

    @Test
    public void ticketModelLayerConstructorAndGettersTestPositive() throws TicketCreateException {
        Ticket testTicketNo1 = new Ticket(ticketIDNo1, movieTimeNo1, clientNo1, movieNo1, ticketTypeNo1);
        assertNotNull(testTicketNo1);
        assertEquals(ticketIDNo1, testTicketNo1.getTicketID());
        assertEquals(movieTimeNo1, testTicketNo1.getMovieTime());
        assertEquals(clientNo1, testTicketNo1.getClient());
        assertEquals(movieNo1, testTicketNo1.getMovie());
        assertEquals(testTicketNo1.getTicketFinalPrice(), movieBasePriceNo1);

        Ticket testTicketNo2 = new Ticket(ticketIDNo2, movieTimeNo2, clientNo2, movieNo2, ticketTypeNo2);
        assertNotNull(testTicketNo2);
        assertEquals(ticketIDNo2, testTicketNo2.getTicketID());
        assertEquals(movieTimeNo2, testTicketNo2.getMovieTime());
        assertEquals(clientNo2, testTicketNo2.getClient());
        assertEquals(movieNo2, testTicketNo2.getMovie());
        assertEquals(testTicketNo2.getTicketFinalPrice(), movieBasePriceNo2 * 0.75);
    }

    @Test
    public void ticketDataLayerConstructorAndGettersTestPositive() {
        Ticket testTicket = new Ticket(ticketIDNo1, movieTimeNo1, ticketFinalPriceNo1, clientNo1, movieNo1);
        assertNotNull(testTicket);
        assertEquals(ticketIDNo1, testTicket.getTicketID());
        assertEquals(movieTimeNo1, testTicket.getMovieTime());
        assertEquals(ticketFinalPriceNo1, testTicket.getTicketFinalPrice());
        assertEquals(clientNo1, testTicket.getClient());
        assertEquals(movieNo1, testTicket.getMovie());
    }

    @Test
    public void ticketModelLayerConstructorWithNullMovieTestNegative() {
        assertThrows(MovieNullReferenceException.class, () -> new Ticket(ticketIDNo1, movieTimeNo1, clientNo1, null, ticketTypeNo1));
    }

    @Test
    public void ticketModelLayerConstructorWithNullTicketTypeTestNegative() {
        assertThrows(MovieNullReferenceException.class, () -> new Ticket(ticketIDNo1, movieTimeNo1, clientNo1, movieNo1, null));
    }

    @Test
    public void ticketMovieTimeSetterTestPositive() {
        LocalDateTime movieTimeBefore = ticketNo1.getMovieTime();
        assertNotNull(movieTimeBefore);
        LocalDateTime newMovieTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusDays(4).plusHours(1);
        assertNotNull(newMovieTime);
        ticketNo1.setMovieTime(newMovieTime);
        LocalDateTime movieTimeAfter = ticketNo1.getMovieTime();
        assertNotNull(movieTimeAfter);
        assertEquals(newMovieTime, movieTimeAfter);
        assertNotEquals(movieTimeBefore, movieTimeAfter);
    }

    @Test
    public void ticketEqualsMethodWithItselfTestPositive() {
        boolean equalsResult = ticketNo1.equals(ticketNo1);
        assertTrue(equalsResult);
    }

    @Test
    public void ticketEqualsMethodWithNullTestNegative() {
        boolean equalsResult = ticketNo1.equals(null);
        assertFalse(equalsResult);
    }

    @Test
    public void ticketEqualsMethodWithObjectOfDifferentClassTestNegative() {
        boolean equalsResult = ticketNo1.equals(new Object());
        assertFalse(equalsResult);
    }

    @Test
    public void ticketEqualsMethodWithObjectOfTheSameClassButDifferentTestNegative() {
        boolean equalsResult = ticketNo1.equals(ticketNo2);
        assertFalse(equalsResult);
    }

    @Test
    public void ticketEqualsMethodWithObjectOfTheSameClassAndTheSameTestPositive() {
        boolean equalsResult = ticketNo1.equals(ticketNo3);
        assertTrue(equalsResult);
    }

    @Test
    public void ticketHashCodeTestPositive() {
        int hashCodeFromTicketNo1 = ticketNo1.hashCode();
        int hashCodeFromTicketNo3 = ticketNo3.hashCode();
        assertEquals(hashCodeFromTicketNo1, hashCodeFromTicketNo3);
        assertEquals(ticketNo1, ticketNo3);
    }

    @Test
    public void ticketHashCodeTestNegative() {
        int hashCodeFromTicketNo1 = ticketNo1.hashCode();
        int hashCodeFromTicketNo2 = ticketNo2.hashCode();
        assertNotEquals(hashCodeFromTicketNo1, hashCodeFromTicketNo2);
    }

    @Test
    public void ticketToStringTestPositive() {
        String ticketToStringResult = ticketNo1.toString();
        assertNotNull(ticketToStringResult);
        assertFalse(ticketToStringResult.isEmpty());
    }
}
