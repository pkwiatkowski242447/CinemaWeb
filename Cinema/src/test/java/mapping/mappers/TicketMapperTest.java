package mapping.mappers;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.pas.gr3.cinema.mapping.docs.MovieDoc;
import pl.pas.gr3.cinema.mapping.docs.TicketDoc;
import pl.pas.gr3.cinema.mapping.docs.users.AdminDoc;
import pl.pas.gr3.cinema.mapping.docs.users.ClientDoc;
import pl.pas.gr3.cinema.mapping.docs.users.StaffDoc;
import pl.pas.gr3.cinema.mapping.mappers.TicketMapper;
import pl.pas.gr3.cinema.model.Movie;
import pl.pas.gr3.cinema.model.Ticket;
import pl.pas.gr3.cinema.model.users.Client;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TicketMapperTest {

    private static UUID uuidNo1;
    private static LocalDateTime movieTimeNo1;
    private static double ticketFinalPriceNo1;
    private static UUID clientId;
    private static UUID movieId;

    private static String loginNo1;
    private static String passwordNo1;
    private static boolean clientStatusActiveNo1;

    private static String movieTitleNo1;
    private static double movieBasePriceNo1;
    private static int scrRoomNumberNo1;
    private static int numberOfAvailableSeatsNo1;

    private TicketDoc ticketDocNo1;
    private Ticket ticketNo1;
    private ClientDoc clientDocNo1;
    private AdminDoc adminDocNo1;
    private StaffDoc staffDocNo1;
    private Client clientNo1;
    private MovieDoc movieDocNo1;
    private Movie movieNo1;

    @BeforeAll
    public static void initializeVariables() {
        uuidNo1 = UUID.randomUUID();
        movieTimeNo1 = LocalDateTime.now().plusDays(3);
        ticketFinalPriceNo1 = 45.75;
        clientId = UUID.randomUUID();
        movieId = UUID.randomUUID();

        loginNo1 = "ExampleLoginNo1";
        passwordNo1 = "ExamplePasswordNo1";
        clientStatusActiveNo1 = true;

        movieTitleNo1 = "ExampleMovieTitleNo1";
        movieBasePriceNo1 = 50.50;
        scrRoomNumberNo1 = 5;
        numberOfAvailableSeatsNo1 = 100;
    }

    @BeforeEach
    public void initializeTicketDocsAndTickets() {
        ticketDocNo1 = new TicketDoc(uuidNo1, movieTimeNo1, ticketFinalPriceNo1, clientId, movieId);
        clientDocNo1 = new ClientDoc(clientId, loginNo1, passwordNo1, clientStatusActiveNo1);
        adminDocNo1 = new AdminDoc(clientId, loginNo1, passwordNo1, clientStatusActiveNo1);
        staffDocNo1 = new StaffDoc(clientId, loginNo1, passwordNo1, clientStatusActiveNo1);
        movieDocNo1 = new MovieDoc(movieId, movieTitleNo1, movieBasePriceNo1, scrRoomNumberNo1, numberOfAvailableSeatsNo1);
        clientNo1 = new Client(clientId, loginNo1, passwordNo1, clientStatusActiveNo1);
        movieNo1 = new Movie(movieId, movieTitleNo1, movieBasePriceNo1, scrRoomNumberNo1, numberOfAvailableSeatsNo1);
        ticketNo1 = new Ticket(uuidNo1, movieTimeNo1, ticketFinalPriceNo1, clientNo1, movieNo1);
    }

    @SuppressWarnings("InstantiationOfUtilityClass")
    @Test
    public void ticketMapperConstructorTest() {
        TicketMapper ticketMapper = new TicketMapper();
        assertNotNull(ticketMapper);
    }

    @Test
    public void ticketMapperTicketToTicketDocTestPositive() {
        TicketDoc ticketDoc = TicketMapper.toTicketDoc(ticketNo1);
        assertNotNull(ticketDoc);
        assertEquals(ticketNo1.getTicketID(), ticketDoc.getTicketID());
        assertEquals(ticketNo1.getMovieTime(), ticketDoc.getMovieTime());
        assertEquals(ticketNo1.getTicketFinalPrice(), ticketDoc.getTicketFinalPrice());
        assertEquals(ticketNo1.getClient().getClientID(), ticketDoc.getClientID());
        assertEquals(ticketNo1.getMovie().getMovieID(), ticketDoc.getMovieID());
    }

    @Test
    public void ticketMapperTicketDocToTicketWithClientDocTestPositive() {
        Ticket ticket = TicketMapper.toTicket(ticketDocNo1, clientDocNo1, movieDocNo1);
        assertNotNull(ticket);
        assertEquals(ticketDocNo1.getTicketID(), ticket.getTicketID());
        assertEquals(ticketDocNo1.getMovieTime(), ticket.getMovieTime());
        assertEquals(ticketDocNo1.getTicketFinalPrice(), ticket.getTicketFinalPrice());
        assertEquals(ticketDocNo1.getClientID(), ticket.getClient().getClientID());
        assertEquals(clientDocNo1.getClientID(), ticket.getClient().getClientID());
        assertEquals(clientDocNo1.getClientLogin(), ticket.getClient().getClientLogin());
        assertEquals(clientDocNo1.getClientPassword(), ticket.getClient().getClientPassword());
        assertEquals(clientDocNo1.isClientStatusActive(), ticket.getClient().isClientStatusActive());
        assertEquals(ticketDocNo1.getMovieID(), ticket.getMovie().getMovieID());
        assertEquals(movieDocNo1.getMovieID(), ticket.getMovie().getMovieID());
        assertEquals(movieDocNo1.getMovieTitle(), ticket.getMovie().getMovieTitle());
        assertEquals(movieDocNo1.getMovieBasePrice(), ticket.getMovie().getMovieBasePrice());
        assertEquals(movieDocNo1.getScrRoomNumber(), ticket.getMovie().getScrRoomNumber());
        assertEquals(movieDocNo1.getNumberOfAvailableSeats(), ticket.getMovie().getNumberOfAvailableSeats());
    }

    @Test
    public void ticketMapperTicketDocToTicketWithAdminDocTestPositive() {
        Ticket ticket = TicketMapper.toTicket(ticketDocNo1, adminDocNo1, movieDocNo1);
        assertNotNull(ticket);
        assertEquals(ticketDocNo1.getTicketID(), ticket.getTicketID());
        assertEquals(ticketDocNo1.getMovieTime(), ticket.getMovieTime());
        assertEquals(ticketDocNo1.getTicketFinalPrice(), ticket.getTicketFinalPrice());
        assertEquals(ticketDocNo1.getClientID(), ticket.getClient().getClientID());
        assertEquals(adminDocNo1.getClientID(), ticket.getClient().getClientID());
        assertEquals(adminDocNo1.getClientLogin(), ticket.getClient().getClientLogin());
        assertEquals(adminDocNo1.getClientPassword(), ticket.getClient().getClientPassword());
        assertEquals(adminDocNo1.isClientStatusActive(), ticket.getClient().isClientStatusActive());
        assertEquals(ticketDocNo1.getMovieID(), ticket.getMovie().getMovieID());
        assertEquals(movieDocNo1.getMovieID(), ticket.getMovie().getMovieID());
        assertEquals(movieDocNo1.getMovieTitle(), ticket.getMovie().getMovieTitle());
        assertEquals(movieDocNo1.getMovieBasePrice(), ticket.getMovie().getMovieBasePrice());
        assertEquals(movieDocNo1.getScrRoomNumber(), ticket.getMovie().getScrRoomNumber());
        assertEquals(movieDocNo1.getNumberOfAvailableSeats(), ticket.getMovie().getNumberOfAvailableSeats());
    }

    @Test
    public void ticketMapperTicketDocToTicketWithStaffDocTestPositive() {
        Ticket ticket = TicketMapper.toTicket(ticketDocNo1, staffDocNo1, movieDocNo1);
        assertNotNull(ticket);
        assertEquals(ticketDocNo1.getTicketID(), ticket.getTicketID());
        assertEquals(ticketDocNo1.getMovieTime(), ticket.getMovieTime());
        assertEquals(ticketDocNo1.getTicketFinalPrice(), ticket.getTicketFinalPrice());
        assertEquals(ticketDocNo1.getClientID(), ticket.getClient().getClientID());
        assertEquals(staffDocNo1.getClientID(), ticket.getClient().getClientID());
        assertEquals(staffDocNo1.getClientLogin(), ticket.getClient().getClientLogin());
        assertEquals(staffDocNo1.getClientPassword(), ticket.getClient().getClientPassword());
        assertEquals(staffDocNo1.isClientStatusActive(), ticket.getClient().isClientStatusActive());
        assertEquals(ticketDocNo1.getMovieID(), ticket.getMovie().getMovieID());
        assertEquals(movieDocNo1.getMovieID(), ticket.getMovie().getMovieID());
        assertEquals(movieDocNo1.getMovieTitle(), ticket.getMovie().getMovieTitle());
        assertEquals(movieDocNo1.getMovieBasePrice(), ticket.getMovie().getMovieBasePrice());
        assertEquals(movieDocNo1.getScrRoomNumber(), ticket.getMovie().getScrRoomNumber());
        assertEquals(movieDocNo1.getNumberOfAvailableSeats(), ticket.getMovie().getNumberOfAvailableSeats());
    }

    @Test
    public void ticketMapperTicketToTicketDocWithNullClientTestNegative() {
        Ticket ticket = new Ticket(uuidNo1, movieTimeNo1, ticketFinalPriceNo1, null, movieNo1);
        TicketDoc ticketDoc = TicketMapper.toTicketDoc(ticket);
        assertNotNull(ticketDoc);
        assertEquals(ticketNo1.getTicketID(), ticketDoc.getTicketID());
        assertEquals(ticketNo1.getMovieTime(), ticketDoc.getMovieTime());
        assertEquals(ticketNo1.getTicketFinalPrice(), ticketDoc.getTicketFinalPrice());
        assertNull(ticketDoc.getClientID());
        assertEquals(ticketNo1.getMovie().getMovieID(), ticketDoc.getMovieID());
    }

    @Test
    public void ticketMapperTicketToTicketDocWithNullMovieTestNegative() {
        Ticket ticket = new Ticket(uuidNo1, movieTimeNo1, ticketFinalPriceNo1, clientNo1, null);
        TicketDoc ticketDoc = TicketMapper.toTicketDoc(ticket);
        assertNotNull(ticketDoc);
        assertEquals(ticketNo1.getTicketID(), ticketDoc.getTicketID());
        assertEquals(ticketNo1.getMovieTime(), ticketDoc.getMovieTime());
        assertEquals(ticketNo1.getTicketFinalPrice(), ticketDoc.getTicketFinalPrice());
        assertEquals(ticketNo1.getClient().getClientID(), ticketDoc.getClientID());
        assertNull(ticketDoc.getMovieID());
    }
}
