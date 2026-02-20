package pl.pas.gr3.cinema.repositories;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.util.ReflectionTestUtils;
import pl.pas.gr3.cinema.entity.Movie;
import pl.pas.gr3.cinema.entity.Ticket;
import pl.pas.gr3.cinema.entity.account.Admin;
import pl.pas.gr3.cinema.entity.account.Client;
import pl.pas.gr3.cinema.exception.bad_request.ClientNotActiveException;
import pl.pas.gr3.cinema.exception.bad_request.MovieDeleteException;
import pl.pas.gr3.cinema.exception.bad_request.TicketCreateException;
import pl.pas.gr3.cinema.exception.bad_request.TicketDeleteException;
import pl.pas.gr3.cinema.exception.bad_request.TicketUpdateException;
import pl.pas.gr3.cinema.exception.not_found.AccountNotFoundException;
import pl.pas.gr3.cinema.exception.not_found.ClientNotFoundException;
import pl.pas.gr3.cinema.exception.not_found.MovieNotFoundException;
import pl.pas.gr3.cinema.exception.not_found.TicketNotFoundException;
import pl.pas.gr3.cinema.mapper.AccountMapper;
import pl.pas.gr3.cinema.mapper.MovieMapper;
import pl.pas.gr3.cinema.mapper.TicketMapper;
import pl.pas.gr3.cinema.repository.impl.AccountRepositoryImpl;
import pl.pas.gr3.cinema.repository.impl.MovieRepositoryImpl;
import pl.pas.gr3.cinema.repository.impl.TicketRepositoryImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class TicketRepositoryTest {

    private static final String DATABASE_NAME = "test";

    private static TicketRepositoryImpl ticketRepositoryForTests;
    private static AccountRepositoryImpl accountRepositoryForTests;
    private static MovieRepositoryImpl movieRepositoryForTests;

    private Client clientNo1;
    private Client clientNo2;

    private Movie movieNo1;
    private Movie movieNo2;

    private Ticket ticketNo1;
    private Ticket ticketNo2;

    private TicketMapper ticketMapper;
    private AccountMapper accountMapper;
    private MovieMapper movieMapper;

    @BeforeAll
    static void init() {
        ticketRepositoryForTests = new TicketRepositoryImpl(DATABASE_NAME);
        accountRepositoryForTests = new AccountRepositoryImpl(DATABASE_NAME);
        movieRepositoryForTests = new MovieRepositoryImpl(DATABASE_NAME);
    }

    @BeforeEach
    void addExampleTickets() {
        accountMapper = Mappers.getMapper(AccountMapper.class);

        ReflectionTestUtils.setField(accountRepositoryForTests, "accountMapper", accountMapper);

        cleanDatabaseState();

        try {
            clientNo1 = accountRepositoryForTests.createClient("ClientLoginNo1", "ClientPasswordNo1");
            clientNo2 = accountRepositoryForTests.createClient("ClientLoginNo2", "ClientPasswordNo2");

            movieNo1 = movieRepositoryForTests.create("MovieTitleNo1", 25.00, 1, 45);
            movieNo2 = movieRepositoryForTests.create("MovieTitleNo2", 35.50, 2, 70);

            LocalDateTime localDateTimeNo1 = LocalDateTime.of(2023, 11, 2, 20, 15, 0);
            LocalDateTime localDateTimeNo2 = LocalDateTime.of(2023, 10, 28, 18, 45, 0);

            ticketNo1 = ticketRepositoryForTests.create(localDateTimeNo1, clientNo1.getId(), movieNo1.getId());
            ticketNo2 = ticketRepositoryForTests.create(localDateTimeNo2, clientNo2.getId(), movieNo2.getId());
        } catch (Exception exception) {
            throw new RuntimeException("Could not initialize test database while adding test data.", exception);
        }
    }

    private void cleanDatabaseState() {
        try {
            List<Ticket> tickets = ticketRepositoryForTests.findAll();
            tickets.forEach(ticket -> ticketRepositoryForTests.delete(ticket.getId()));

            List<Client> clients = accountRepositoryForTests.findAllClients();
            clients.forEach(client -> accountRepositoryForTests.delete(client.getId(), "client"));

            List<Movie> movies = movieRepositoryForTests.findAll();
            movies.forEach(movie -> movieRepositoryForTests.delete(movie.getId()));
        } catch (Exception exception) {
            log.error("Could not remove all tickets from the test database after ticket repository tests.", exception);
        }
    }

    @AfterAll
    static void destroy() {
        ticketRepositoryForTests.close();
        accountRepositoryForTests.close();
        movieRepositoryForTests.close();
    }

    @Test
    void ticketRepositoryCreateTicketTestPositive() {
        LocalDateTime localDateTime = LocalDateTime.of(2023, 11, 4, 20, 10, 0);
        Ticket ticket = ticketRepositoryForTests.create(localDateTime, clientNo1.getId(), movieNo1.getId());
        assertNotNull(ticket);
        Ticket foundTicket = ticketRepositoryForTests.findByUUID(ticket.getId());
        assertNotNull(foundTicket);
        assertEquals(ticket, foundTicket);
    }

    @Test
    void ticketRepositoryCreateTicketWithInactiveClientTestNegative() {
        LocalDateTime localDateTime = LocalDateTime.of(2023, 11, 4, 20, 10, 0);
        accountRepositoryForTests.deactivate(clientNo1, "client");
        assertThrows(ClientNotActiveException.class, () -> ticketRepositoryForTests.create(localDateTime, clientNo1.getId(), movieNo1.getId()));
    }

    @Test
    void ticketRepositoryCreateTicketWithNullMovieTimeTestNegative() {
        assertThrows(TicketCreateException.class, () -> ticketRepositoryForTests.create(null, clientNo1.getId(), movieNo1.getId()));
    }

    @Test
    void ticketRepositoryCreateTicketWithNullClientTestNegative() {
        LocalDateTime localDateTime = LocalDateTime.of(2023, 11, 4, 20, 10, 0);
        assertThrows(ClientNotFoundException.class, () -> ticketRepositoryForTests.create(localDateTime, null, movieNo1.getId()));
    }

    @Test
    void ticketRepositoryCreateTicketWithNullMovieTestNegative() {
        LocalDateTime localDateTime = LocalDateTime.of(2023, 11, 4, 20, 10, 0);
        assertThrows(MovieNotFoundException.class, () -> ticketRepositoryForTests.create(localDateTime, clientNo1.getId(), null));
    }

    @Test
    void ticketRepositoryFindTicketTestPositive() {
        Ticket foundTicket = ticketRepositoryForTests.findByUUID(ticketNo1.getId());
        assertNotNull(foundTicket);
        assertEquals(ticketNo1, foundTicket);
    }

    @Test
    void ticketRepositoryFindTicketThatIsNotInTheDatabaseTestNegative() {
        LocalDateTime localDateTime = LocalDateTime.of(2023, 11, 4, 20, 10, 0);
        assertNotNull(localDateTime);
        Ticket ticket = new Ticket(UUID.randomUUID(), localDateTime, movieNo1.getBasePrice(), clientNo1.getId(), movieNo1.getId());
        assertNotNull(ticket);
        assertThrows(TicketNotFoundException.class, () -> ticketRepositoryForTests.findByUUID(ticket.getId()));
    }

    @Test
    void ticketRepositoryFindAllTicketsTestPositive() {
        List<Ticket> listOfAllTickets = ticketRepositoryForTests.findAll();
        assertNotNull(listOfAllTickets);
        assertFalse(listOfAllTickets.isEmpty());
        assertEquals(2, listOfAllTickets.size());
    }

    @Test
    void ticketRepositoryUpdateTicketTestPositive() {
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
    void ticketRepositoryUpdateTicketThatIsNotInTheDatabaseTestNegative() {
        LocalDateTime localDateTime = LocalDateTime.of(2023, 11, 4, 20, 10, 0);
        assertNotNull(localDateTime);
        Ticket ticket = new Ticket(UUID.randomUUID(), localDateTime, movieNo1.getBasePrice(), clientNo1.getId(), movieNo1.getId());
        assertNotNull(ticket);
        assertThrows(TicketNotFoundException.class, () -> ticketRepositoryForTests.update(ticket));
    }

    @Test
    void ticketRepositoryUpdateTicketWithNullLocalDateTimeTestNegative() {
        ticketNo1.setMovieTime(null);
        assertThrows(TicketUpdateException.class, () -> ticketRepositoryForTests.update(ticketNo1));
    }

    @Test
    void ticketRepositoryDeleteTicketTestPositive() {
        int numberOfTicketsBefore = ticketRepositoryForTests.findAll().size();
        UUID removedTicketID = ticketNo1.getId();
        ticketRepositoryForTests.delete(ticketNo1.getId());
        int numberOfTicketsAfter = ticketRepositoryForTests.findAll().size();
        assertNotEquals(numberOfTicketsBefore, numberOfTicketsAfter);
        assertEquals(2, numberOfTicketsBefore);
        assertEquals(1, numberOfTicketsAfter);
        assertThrows(TicketNotFoundException.class, () -> ticketRepositoryForTests.findByUUID(removedTicketID));
    }

    @Test
    void ticketRepositoryDeleteTicketThatIsNotInTheDatabaseTestNegative() {
        LocalDateTime localDateTime = LocalDateTime.of(2023, 11, 4, 20, 10, 0);
        assertNotNull(localDateTime);
        Ticket ticket = new Ticket(UUID.randomUUID(), localDateTime, movieNo1.getBasePrice(), clientNo1.getId(), movieNo1.getId());
        assertNotNull(ticket);
        assertThrows(TicketNotFoundException.class, () -> ticketRepositoryForTests.delete(ticket.getId()));
    }

    @Test
    void movieRepositoryDeleteMovieThatIsUsedByTheTicketInTheDatabaseTestNegative() {
        assertThrows(MovieDeleteException.class, () -> movieRepositoryForTests.delete(movieNo1.getId()));
    }

    @Test
    void clientRepositoryFindAllTicketsWithGivenClient() {
        List<Ticket> listOfActiveTicketsNo1 = accountRepositoryForTests.getListOfTicketsForClient(clientNo1.getId(), "client");
        assertNotNull(listOfActiveTicketsNo1);
        assertFalse(listOfActiveTicketsNo1.isEmpty());
        assertEquals(1, listOfActiveTicketsNo1.size());
        ticketRepositoryForTests.create(ticketNo2.getMovieTime(), clientNo1.getId(), movieNo2.getId());
        List<Ticket> listOfActiveTicketsNo2 = accountRepositoryForTests.getListOfTicketsForClient(clientNo1.getId(), "client");
        assertNotNull(listOfActiveTicketsNo2);
        assertFalse(listOfActiveTicketsNo2.isEmpty());
        assertEquals(2, listOfActiveTicketsNo2.size());
    }

    @Test
    void clientRepositoryFindAllTicketsWithGivenClientThatIsNotInTheDatabaseTestNegative() {
        Client client = new Client(UUID.randomUUID(), "SomeRandomLogin", "SomeRandomPassword");
        assertNotNull(client);
        assertThrows(ClientNotFoundException.class, () -> accountRepositoryForTests.getListOfTicketsForClient(client.getId(), "client"));
    }

    @Test
    void clientRepositoryFindAllTicketsWithGivenClientThatIsInTheDatabaseAndIncorrectNameTestNegative() {
        Admin admin = accountRepositoryForTests.createAdmin("SomeLogin", "SomePassword");
        assertNotNull(admin);
        assertThrows(ClientNotFoundException.class, () -> accountRepositoryForTests.getListOfTicketsForClient(admin.getId(), "client"));
    }

    @Test
    void movieRepositoryFindAllTicketsWithGivenMovie() {
        List<Ticket> listOfActiveTicketsNo1 = movieRepositoryForTests.getListOfTicketsForMovie(movieNo1.getId());
        assertNotNull(listOfActiveTicketsNo1);
        assertFalse(listOfActiveTicketsNo1.isEmpty());
        assertEquals(1, listOfActiveTicketsNo1.size());
        ticketRepositoryForTests.create(ticketNo2.getMovieTime(), clientNo2.getId(), movieNo1.getId());
        List<Ticket> listOfActiveTicketsNo2 = movieRepositoryForTests.getListOfTicketsForMovie(movieNo1.getId());
        assertNotNull(listOfActiveTicketsNo2);
        assertFalse(listOfActiveTicketsNo2.isEmpty());
        assertEquals(2, listOfActiveTicketsNo2.size());
    }
}