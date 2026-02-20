package pl.pas.gr3.cinema.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.pas.gr3.cinema.entity.Movie;
import pl.pas.gr3.cinema.entity.Ticket;
import pl.pas.gr3.cinema.entity.account.Client;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TicketTest {

    private static UUID ticketIDNo1;
    private static UUID ticketIDNo2;
    private static LocalDateTime movieTimeNo1;
    private static LocalDateTime movieTimeNo2;

    private static double ticketFinalPriceNo1;

    private static Client clientNo1;
    private static Client clientNo2;
    private static Movie movieNo1;
    private static Movie movieNo2;

    private static double movieBasePriceNo1;
    private static double movieBasePriceNo2;

    private static int numberOfAvailableSeatsNo1;
    private static int numberOfAvailableSeatsNo2;

    private Ticket ticketNo1;
    private Ticket ticketNo2;
    private Ticket ticketNo3;

    @BeforeAll
    static void initializeVariables() {
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
    }

    @BeforeEach
    void initializeTickets()  {
        ticketNo1 = new Ticket(ticketIDNo1, movieTimeNo1, movieNo1.getBasePrice(), clientNo1.getId(), movieNo1.getId());
        ticketNo2 = new Ticket(ticketIDNo2, movieTimeNo2, movieNo1.getBasePrice(), clientNo2.getId(), movieNo2.getId());
        ticketNo3 = new Ticket(ticketNo1.getId(),
                ticketNo1.getMovieTime(),
                ticketNo1.getPrice(),
                ticketNo1.getUserId(),
                ticketNo1.getMovieId());
    }

    @Test
    void ticketModelLayerConstructorAndGettersTestPositive() {
        Ticket testTicketNo1 = new Ticket(ticketIDNo1, movieTimeNo1, movieNo1.getBasePrice(), clientNo1.getId(), movieNo1.getId());
        assertNotNull(testTicketNo1);
        assertEquals(ticketIDNo1, testTicketNo1.getId());
        assertEquals(movieTimeNo1, testTicketNo1.getMovieTime());
        assertEquals(clientNo1.getId(), testTicketNo1.getUserId());
        assertEquals(movieNo1.getId(), testTicketNo1.getMovieId());
        assertEquals(testTicketNo1.getPrice(), movieBasePriceNo1);

        Ticket testTicketNo2 = new Ticket(ticketIDNo2, movieTimeNo2, movieNo2.getBasePrice(), clientNo2.getId(), movieNo2.getId());
        assertNotNull(testTicketNo2);
        assertEquals(ticketIDNo2, testTicketNo2.getId());
        assertEquals(movieTimeNo2, testTicketNo2.getMovieTime());
        assertEquals(clientNo2.getId(), testTicketNo2.getUserId());
        assertEquals(movieNo2.getId(), testTicketNo2.getMovieId());
        assertEquals(testTicketNo2.getPrice(), movieBasePriceNo2);
    }

    @Test
    void ticketDataLayerConstructorAndGettersTestPositive() {
        Ticket testTicket = new Ticket(ticketIDNo1, movieTimeNo1, ticketFinalPriceNo1, clientNo1.getId(), movieNo1.getId());
        assertNotNull(testTicket);
        assertEquals(ticketIDNo1, testTicket.getId());
        assertEquals(movieTimeNo1, testTicket.getMovieTime());
        assertEquals(ticketFinalPriceNo1, testTicket.getPrice());
        assertEquals(clientNo1.getId(), testTicket.getUserId());
        assertEquals(movieNo1.getId(), testTicket.getMovieId());
    }

    @Test
    void ticketMovieTimeSetterTestPositive() {
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
    void ticketEqualsMethodWithItselfTestPositive() {
        boolean equalsResult = ticketNo1.equals(ticketNo1);
        assertTrue(equalsResult);
    }

    @Test
    void ticketEqualsMethodWithNullTestNegative() {
        boolean equalsResult = ticketNo1.equals(null);
        assertFalse(equalsResult);
    }

    @Test
    void ticketEqualsMethodWithObjectOfDifferentClassTestNegative() {
        boolean equalsResult = ticketNo1.equals(new Object());
        assertFalse(equalsResult);
    }

    @Test
    void ticketEqualsMethodWithObjectOfTheSameClassButDifferentTestNegative() {
        boolean equalsResult = ticketNo1.equals(ticketNo2);
        assertFalse(equalsResult);
    }

    @Test
    void ticketEqualsMethodWithObjectOfTheSameClassAndTheSameTestPositive() {
        boolean equalsResult = ticketNo1.equals(ticketNo3);
        assertTrue(equalsResult);
    }

    @Test
    void ticketHashCodeTestPositive() {
        int hashCodeFromTicketNo1 = ticketNo1.hashCode();
        int hashCodeFromTicketNo3 = ticketNo3.hashCode();
        assertEquals(hashCodeFromTicketNo1, hashCodeFromTicketNo3);
        assertEquals(ticketNo1, ticketNo3);
    }

    @Test
    void ticketHashCodeTestNegative() {
        int hashCodeFromTicketNo1 = ticketNo1.hashCode();
        int hashCodeFromTicketNo2 = ticketNo2.hashCode();
        assertNotEquals(hashCodeFromTicketNo1, hashCodeFromTicketNo2);
    }

    @Test
    void ticketToStringTestPositive() {
        String ticketToStringResult = ticketNo1.toString();
        assertNotNull(ticketToStringResult);
        assertFalse(ticketToStringResult.isEmpty());
    }
}