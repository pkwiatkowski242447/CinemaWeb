package repositories;

import org.junit.jupiter.api.*;
import pl.pas.gr3.cinema.exceptions.model.TicketCreateException;
import pl.pas.gr3.cinema.exceptions.repositories.*;
import pl.pas.gr3.cinema.exceptions.repositories.crud.client.ClientRepositoryReadException;
import pl.pas.gr3.cinema.exceptions.repositories.crud.movie.MovieRepositoryDeleteException;
import pl.pas.gr3.cinema.exceptions.repositories.crud.ticket.TicketRepositoryCreateException;
import pl.pas.gr3.cinema.exceptions.repositories.crud.ticket.TicketRepositoryDeleteException;
import pl.pas.gr3.cinema.exceptions.repositories.crud.ticket.TicketRepositoryReadException;
import pl.pas.gr3.cinema.exceptions.repositories.crud.ticket.TicketRepositoryUpdateException;
import pl.pas.gr3.cinema.model.Movie;
import pl.pas.gr3.cinema.model.Ticket;
import pl.pas.gr3.cinema.model.TicketType;
import pl.pas.gr3.cinema.model.users.Admin;
import pl.pas.gr3.cinema.model.users.Client;
import pl.pas.gr3.cinema.repositories.implementations.ClientRepository;
import pl.pas.gr3.cinema.repositories.implementations.MovieRepository;
import pl.pas.gr3.cinema.repositories.implementations.TicketRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TicketRepositoryTest {

    private final static String databaseName = "test";

    private static TicketRepository ticketRepositoryForTests;
    private static ClientRepository clientRepositoryForTests;
    private static MovieRepository movieRepositoryForTests;

    private Client clientNo1;
    private Client clientNo2;

    private Movie movieNo1;
    private Movie movieNo2;

    private Ticket ticketNo1;
    private Ticket ticketNo2;

    @BeforeAll
    public static void init() {
        ticketRepositoryForTests = new TicketRepository(databaseName);
        clientRepositoryForTests = new ClientRepository(databaseName);
        movieRepositoryForTests = new MovieRepository(databaseName);
    }

    @BeforeEach
    public void addExampleTickets() {
        try {
            clientNo1 = clientRepositoryForTests.createClient("ClientLoginNo1", "ClientPasswordNo1");
            clientNo2 = clientRepositoryForTests.createClient("ClientLoginNo2", "ClientPasswordNo2");
        } catch (ClientRepositoryException exception) {
            throw new RuntimeException("Could not initialize test database while adding example clients to it.", exception);
        }

        try {
            movieNo1 = movieRepositoryForTests.create("MovieTitleNo1", 25.00, 1, 45);
            movieNo2 = movieRepositoryForTests.create("MovieTitleNo2", 35.50, 2, 70);
        } catch (MovieRepositoryException exception) {
            throw new RuntimeException("Could not initialize test database while adding example movies to it.", exception);
        }

        LocalDateTime localDateTimeNo1 = LocalDateTime.of(2023, 11, 2, 20, 15, 0);
        LocalDateTime localDateTimeNo2 = LocalDateTime.of(2023, 10, 28, 18, 45, 0);

        try {
            ticketNo1 = ticketRepositoryForTests.create(localDateTimeNo1, clientNo1.getClientID(), movieNo1.getMovieID(), TicketType.NORMAL);
            ticketNo2 = ticketRepositoryForTests.create(localDateTimeNo2, clientNo2.getClientID(), movieNo2.getMovieID(), TicketType.NORMAL);
        } catch (TicketRepositoryException exception) {
            throw new RuntimeException("Could not initialize test database while adding example tickets to it.", exception);
        }
    }

    @AfterEach
    public void removeExampleTickets() {
        try {
            List<Ticket> listOfAllTickets = ticketRepositoryForTests.findAll();
            for (Ticket ticket : listOfAllTickets) {
                ticketRepositoryForTests.delete(ticket.getTicketID());
            }
        } catch (TicketRepositoryException exception) {
            throw new RuntimeException("Could not remove all tickets from the test database after ticket repository tests.", exception);
        }

        try {
            List<Client> listOfAllClients = clientRepositoryForTests.findAllClients();
            for (Client client : listOfAllClients) {
                clientRepositoryForTests.delete(client.getClientID(), "client");
            }
        } catch (ClientRepositoryException exception) {
            throw new RuntimeException("Could not remove all clients from the test database after ticket repository tests.", exception);
        }

        try {
            List<Movie> listOfAllMovies = movieRepositoryForTests.findAll();
            for (Movie movie : listOfAllMovies) {
                movieRepositoryForTests.delete(movie.getMovieID());
            }
        } catch (MovieRepositoryException exception) {
            throw new RuntimeException("Could not remove all movies from the test database after ticket repository tests.", exception);
        }
    }

    @AfterAll
    public static void destroy() {
        ticketRepositoryForTests.close();
        clientRepositoryForTests.close();
        movieRepositoryForTests.close();
    }

    @Test
    public void ticketRepositoryCreateTicketTestPositive() throws TicketRepositoryException {
        LocalDateTime localDateTime = LocalDateTime.of(2023, 11, 4, 20, 10, 0);
        Ticket ticket = ticketRepositoryForTests.create(localDateTime, clientNo1.getClientID(), movieNo1.getMovieID(), TicketType.NORMAL);
        assertNotNull(ticket);
        Ticket foundTicket = ticketRepositoryForTests.findByUUID(ticket.getTicketID());
        assertNotNull(foundTicket);
        assertEquals(ticket, foundTicket);
    }

    @Test
    public void ticketRepositoryCreateTicketWithInactiveClientTestNegative() throws ClientRepositoryException {
        LocalDateTime localDateTime = LocalDateTime.of(2023, 11, 4, 20, 10, 0);
        clientRepositoryForTests.deactivate(clientNo1, "client");
        assertThrows(TicketRepositoryCreateException.class, () -> ticketRepositoryForTests.create(localDateTime, clientNo1.getClientID(), movieNo1.getMovieID(), TicketType.NORMAL));
    }

    @Test
    public void ticketRepositoryCreateTicketWithNullMovieTimeTestNegative() {
        assertThrows(TicketRepositoryCreateException.class, () -> ticketRepositoryForTests.create(null, clientNo1.getClientID(), movieNo1.getMovieID(), TicketType.NORMAL));
    }

    @Test
    public void ticketRepositoryCreateTicketWithNullClientTestNegative() {
        LocalDateTime localDateTime = LocalDateTime.of(2023, 11, 4, 20, 10, 0);
        assertThrows(TicketRepositoryCreateException.class, () -> ticketRepositoryForTests.create(localDateTime, null, movieNo1.getMovieID(), TicketType.NORMAL));
    }

    @Test
    public void ticketRepositoryCreateTicketWithNullMovieTestNegative() {
        LocalDateTime localDateTime = LocalDateTime.of(2023, 11, 4, 20, 10, 0);
        assertThrows(TicketRepositoryCreateException.class, () -> ticketRepositoryForTests.create(localDateTime, clientNo1.getClientID(), null, TicketType.NORMAL));
    }

    @Test
    public void ticketRepositoryCreateTicketWithNullTicketTypeTestNegative() {
        LocalDateTime localDateTime = LocalDateTime.of(2023, 11, 4, 20, 10, 0);
        assertThrows(TicketRepositoryCreateException.class, () -> ticketRepositoryForTests.create(localDateTime, clientNo1.getClientID(), movieNo1.getMovieID(), null));
    }

    @Test
    public void ticketRepositoryFindTicketTestPositive() throws TicketRepositoryException {
        Ticket foundTicket = ticketRepositoryForTests.findByUUID(ticketNo1.getTicketID());
        assertNotNull(foundTicket);
        assertEquals(ticketNo1, foundTicket);
    }

    @Test
    public void ticketRepositoryFindTicketThatIsNotInTheDatabaseTestNegative() throws TicketCreateException {
        LocalDateTime localDateTime = LocalDateTime.of(2023, 11, 4, 20, 10, 0);
        assertNotNull(localDateTime);
        Ticket ticket = new Ticket(UUID.randomUUID(), localDateTime, clientNo1, movieNo1, TicketType.NORMAL);
        assertNotNull(ticket);
        assertThrows(TicketRepositoryReadException.class, () -> ticketRepositoryForTests.findByUUID(ticket.getTicketID()));
    }

    @Test
    public void ticketRepositoryFindAllTicketUUIDsTestPositive() throws TicketRepositoryException {
        List<UUID> listOfAllUUIDs = ticketRepositoryForTests.findAllUUIDs();
        assertNotNull(listOfAllUUIDs);
        assertFalse(listOfAllUUIDs.isEmpty());
        assertEquals(2, listOfAllUUIDs.size());
    }

    @Test
    public void ticketRepositoryFindAllTicketsTestPositive() throws TicketRepositoryException {
        List<Ticket> listOfAllTickets = ticketRepositoryForTests.findAll();
        assertNotNull(listOfAllTickets);
        assertFalse(listOfAllTickets.isEmpty());
        assertEquals(2, listOfAllTickets.size());
    }

    @Test
    public void ticketRepositoryUpdateTicketTestPositive() throws TicketRepositoryException {
        LocalDateTime localDateTime = LocalDateTime.now();
        assertNotNull(localDateTime);
        LocalDateTime movieTimeBefore = ticketNo1.getMovieTime();
        ticketNo1.setMovieTime(localDateTime);
        ticketRepositoryForTests.update(ticketNo1);
        LocalDateTime movieTimeAfter = ticketNo1.getMovieTime();
        assertNotNull(movieTimeAfter);
        assertEquals(localDateTime, movieTimeAfter);
        assertNotEquals(movieTimeBefore, movieTimeAfter);
    }

    @Test
    public void ticketRepositoryUpdateTicketThatIsNotInTheDatabaseTestNegative() throws TicketCreateException {
        LocalDateTime localDateTime = LocalDateTime.of(2023, 11, 4, 20, 10, 0);
        assertNotNull(localDateTime);
        Ticket ticket = new Ticket(UUID.randomUUID(), localDateTime, clientNo1, movieNo1, TicketType.NORMAL);
        assertNotNull(ticket);
        assertThrows(TicketRepositoryUpdateException.class, () -> ticketRepositoryForTests.update(ticket));
    }

    @Test
    public void ticketRepositoryUpdateTicketWithNullLocalDateTimeTestNegative() {
        ticketNo1.setMovieTime(null);
        assertThrows(TicketRepositoryUpdateException.class, () -> ticketRepositoryForTests.update(ticketNo1));
    }

    @Test
    public void ticketRepositoryDeleteTicketTestPositive() throws TicketRepositoryException {
        int numberOfTicketsBefore = ticketRepositoryForTests.findAll().size();
        UUID removedTicketID = ticketNo1.getTicketID();
        ticketRepositoryForTests.delete(ticketNo1.getTicketID());
        int numberOfTicketsAfter = ticketRepositoryForTests.findAll().size();
        assertNotEquals(numberOfTicketsBefore, numberOfTicketsAfter);
        assertEquals(2, numberOfTicketsBefore);
        assertEquals(1, numberOfTicketsAfter);
        assertThrows(TicketRepositoryReadException.class, () -> ticketRepositoryForTests.findByUUID(removedTicketID));
    }

    @Test
    public void ticketRepositoryDeleteTicketThatIsNotInTheDatabaseTestNegative() throws TicketCreateException {
        LocalDateTime localDateTime = LocalDateTime.of(2023, 11, 4, 20, 10, 0);
        assertNotNull(localDateTime);
        Ticket ticket = new Ticket(UUID.randomUUID(), localDateTime, clientNo1, movieNo1, TicketType.NORMAL);
        assertNotNull(ticket);
        assertThrows(TicketRepositoryDeleteException.class, () -> ticketRepositoryForTests.delete(ticket.getTicketID()));
    }

    @Test
    public void movieRepositoryDeleteMovieThatIsUsedByTheTicketInTheDatabaseTestNegative() {
        assertThrows(MovieRepositoryDeleteException.class, () -> movieRepositoryForTests.delete(movieNo1.getMovieID()));
    }

    @Test
    public void clientRepositoryFindAllTicketsWithGivenClient() throws TicketRepositoryException, ClientRepositoryException {
        List<Ticket> listOfActiveTicketsNo1 = clientRepositoryForTests.getListOfTicketsForClient(clientNo1.getClientID(), "client");
        assertNotNull(listOfActiveTicketsNo1);
        assertFalse(listOfActiveTicketsNo1.isEmpty());
        assertEquals(1, listOfActiveTicketsNo1.size());
        ticketRepositoryForTests.create(ticketNo2.getMovieTime(), clientNo1.getClientID(), movieNo2.getMovieID(), TicketType.NORMAL);
        List<Ticket> listOfActiveTicketsNo2 = clientRepositoryForTests.getListOfTicketsForClient(clientNo1.getClientID(), "client");
        assertNotNull(listOfActiveTicketsNo2);
        assertFalse(listOfActiveTicketsNo2.isEmpty());
        assertEquals(2, listOfActiveTicketsNo2.size());
    }

    @Test
    public void clientRepositoryFindALlTicketsWithGivenClientThatIsNotInTheDatabaseTestNegative() {
        Client client = new Client(UUID.randomUUID(), "SomeRandomLogin", "SomeRandomPassword");
        assertNotNull(client);
        assertThrows(ClientRepositoryReadException.class, () -> clientRepositoryForTests.getListOfTicketsForClient(client.getClientID(), "client"));
    }

    @Test
    public void clientRepositoryFindALlTicketsWithGivenClientThatIsInTheDatabaseAndIncorrectNameTestNegative() throws ClientRepositoryException{
        Admin admin = clientRepositoryForTests.createAdmin("SomeLogin", "SomePassword");
        assertNotNull(admin);
        assertThrows(ClientRepositoryReadException.class, () -> clientRepositoryForTests.getListOfTicketsForClient(admin.getClientID(), "client"));
    }

    @Test
    public void movieRepositoryFindAllTicketsWithGivenMovie() throws TicketRepositoryException {
        List<Ticket> listOfActiveTicketsNo1 = movieRepositoryForTests.getListOfTicketsForMovie(movieNo1.getMovieID());
        assertNotNull(listOfActiveTicketsNo1);
        assertFalse(listOfActiveTicketsNo1.isEmpty());
        assertEquals(1, listOfActiveTicketsNo1.size());
        ticketRepositoryForTests.create(ticketNo2.getMovieTime(), clientNo2.getClientID(), movieNo1.getMovieID(), TicketType.NORMAL);
        List<Ticket> listOfActiveTicketsNo2 = movieRepositoryForTests.getListOfTicketsForMovie(movieNo1.getMovieID());
        assertNotNull(listOfActiveTicketsNo2);
        assertFalse(listOfActiveTicketsNo2.isEmpty());
        assertEquals(2, listOfActiveTicketsNo2.size());
    }
}