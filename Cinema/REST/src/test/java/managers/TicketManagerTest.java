package managers;

import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pas.gr3.cinema.exceptions.managers.crud.admin.AdminManagerCreateException;
import pl.pas.gr3.cinema.exceptions.managers.crud.admin.AdminManagerDeleteException;
import pl.pas.gr3.cinema.exceptions.managers.crud.admin.AdminManagerReadException;
import pl.pas.gr3.cinema.exceptions.managers.crud.client.ClientManagerCreateException;
import pl.pas.gr3.cinema.exceptions.managers.crud.client.ClientManagerDeleteException;
import pl.pas.gr3.cinema.exceptions.managers.crud.client.ClientManagerReadException;
import pl.pas.gr3.cinema.exceptions.managers.crud.movie.MovieManagerCreateException;
import pl.pas.gr3.cinema.exceptions.managers.crud.movie.MovieManagerDeleteException;
import pl.pas.gr3.cinema.exceptions.managers.crud.movie.MovieManagerReadException;
import pl.pas.gr3.cinema.exceptions.managers.crud.staff.StaffManagerCreateException;
import pl.pas.gr3.cinema.exceptions.managers.crud.staff.StaffManagerDeleteException;
import pl.pas.gr3.cinema.exceptions.managers.crud.staff.StaffManagerReadException;
import pl.pas.gr3.cinema.exceptions.managers.crud.ticket.TicketManagerCreateException;
import pl.pas.gr3.cinema.exceptions.managers.crud.ticket.TicketManagerDeleteException;
import pl.pas.gr3.cinema.exceptions.managers.crud.ticket.TicketManagerReadException;
import pl.pas.gr3.cinema.exceptions.managers.crud.ticket.TicketManagerUpdateException;
import pl.pas.gr3.cinema.exceptions.model.TicketCreateException;
import pl.pas.gr3.cinema.managers.implementations.*;
import pl.pas.gr3.cinema.model.Movie;
import pl.pas.gr3.cinema.model.Ticket;
import pl.pas.gr3.cinema.model.TicketType;
import pl.pas.gr3.cinema.model.users.Admin;
import pl.pas.gr3.cinema.model.users.Client;
import pl.pas.gr3.cinema.model.users.Staff;
import pl.pas.gr3.cinema.repositories.implementations.ClientRepository;
import pl.pas.gr3.cinema.repositories.implementations.MovieRepository;
import pl.pas.gr3.cinema.repositories.implementations.TicketRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TicketManagerTest {

    private static final String databaseName = "test";
    private static final Logger logger = LoggerFactory.getLogger(TicketManagerTest.class);
    private static ClientRepository clientRepository;
    private static MovieRepository movieRepository;
    private static TicketRepository ticketRepository;

    private static ClientManager clientManager;
    private static AdminManager adminManager;
    private static StaffManager staffManager;
    private static MovieManager movieManager;
    private static TicketManager ticketManager;

    private Client clientNo1;
    private Client clientNo2;

    private Admin adminNo1;
    private Admin adminNo2;

    private Staff staffNo1;
    private Staff staffNo2;

    private Movie movieNo1;
    private Movie movieNo2;

    private Ticket ticketNo1;
    private Ticket ticketNo2;
    private Ticket ticketNo3;
    private Ticket ticketNo4;
    private Ticket ticketNo5;
    private Ticket ticketNo6;

    private LocalDateTime movieTimeNo1;
    private LocalDateTime movieTimeNo2;

    @BeforeAll
    public static void initialize() {
        clientRepository = new ClientRepository(databaseName);
        movieRepository = new MovieRepository(databaseName);
        ticketRepository = new TicketRepository(databaseName);

        clientManager = new ClientManager(clientRepository);
        adminManager = new AdminManager(clientRepository);
        staffManager = new StaffManager(clientRepository);
        movieManager = new MovieManager(movieRepository);
        ticketManager = new TicketManager(ticketRepository);
    }

    @BeforeEach
    public void initializeSampleData() {
        try {
            clientNo1 = clientManager.create("UniqueClientLoginNo1", "UniqueClientPasswordNo1");
            clientNo2 = clientManager.create("UniqueClientLoginNo2", "UniqueClientPasswordNo2");
        } catch (ClientManagerCreateException exception) {
            logger.error(exception.getMessage());
        }

        try {
            adminNo1 = adminManager.create("UniqueAdminLoginNo1", "UniqueAdminPasswordNo1");
            adminNo2 = adminManager.create("UniqueAdminLoginNo2", "UniqueAdminPasswordNo2");
        } catch (AdminManagerCreateException exception) {
            logger.error(exception.getMessage());
        }

        try {
            staffNo1 = staffManager.create("UniqueStaffLoginNo1", "UniqueStaffPasswordNo1");
            staffNo2 = staffManager.create("UniqueStaffLoginNo2", "UniqueStaffPasswordNo2");
        } catch (StaffManagerCreateException exception) {
            logger.error(exception.getMessage());
        }

        try {
            movieNo1 = movieManager.create("UniqueMovieTitleNo1", 45.00, 1, 60);
            movieNo2 = movieManager.create("UniqueMovieTitleNo2", 35.50, 2, 40);
        } catch (MovieManagerCreateException exception) {
            logger.error(exception.getMessage());
        }

        movieTimeNo1 = LocalDateTime.now().plusHours(3).plusDays(2).truncatedTo(ChronoUnit.SECONDS);
        movieTimeNo2 = LocalDateTime.now().plusHours(8).plusDays(5).truncatedTo(ChronoUnit.SECONDS);

        try {
            ticketNo1 = ticketManager.create(movieTimeNo1.toString(), clientNo1.getClientID(), movieNo1.getMovieID(), "normal");
            ticketNo2 = ticketManager.create(movieTimeNo2.toString(), clientNo1.getClientID(), movieNo2.getMovieID(), "normal");

            ticketNo3 = ticketManager.create(movieTimeNo1.toString(), adminNo1.getClientID(), movieNo1.getMovieID(), "normal");
            ticketNo4 = ticketManager.create(movieTimeNo2.toString(), adminNo1.getClientID(), movieNo2.getMovieID(), "normal");

            ticketNo5 = ticketManager.create(movieTimeNo1.toString(), staffNo1.getClientID(), movieNo1.getMovieID(), "normal");
            ticketNo6 = ticketManager.create(movieTimeNo2.toString(), staffNo1.getClientID(), movieNo2.getMovieID(), "normal");
        } catch (TicketManagerCreateException exception) {
            logger.error(exception.getMessage());
        }
    }

    @AfterEach
    public void destroySampleData() {

        try {
            List<Ticket> listOfTickets = ticketManager.findAll();
            for (Ticket ticket : listOfTickets) {
                ticketManager.delete(ticket.getTicketID());
            }
        } catch (TicketManagerDeleteException | TicketManagerReadException exception) {
            logger.error(exception.getMessage());
        }

        try {
            List<Movie> listOfMovies = movieManager.findAll();
            for (Movie movie : listOfMovies) {
                movieManager.delete(movie.getMovieID());
            }
        } catch (MovieManagerDeleteException | MovieManagerReadException exception) {
            logger.error(exception.getMessage());
        }

        try {
            List<Client> listOfClient = clientManager.findAll();
            for (Client client : listOfClient) {
                clientManager.delete(client.getClientID());
            }
        } catch (ClientManagerDeleteException | ClientManagerReadException exception) {
            logger.error(exception.getMessage());
        }

        try {
            List<Admin> listOfAdmins = adminManager.findAll();
            for (Admin admin : listOfAdmins) {
                adminManager.delete(admin.getClientID());
            }
        } catch (AdminManagerDeleteException | AdminManagerReadException exception) {
            logger.error(exception.getMessage());
        }

        try {
            List<Staff> listOfStaffs = staffManager.findAll();
            for (Staff staff : listOfStaffs) {
                staffManager.delete(staff.getClientID());
            }
        } catch (StaffManagerDeleteException | StaffManagerReadException exception) {
            logger.error(exception.getMessage());
        }
    }

    @AfterAll
    public static void destroy() {
        clientRepository.close();
        movieManager.close();
        ticketManager.close();
    }

    // Create tests

    @Test
    public void ticketManagerCreateTicketNormalTestPositive() throws TicketManagerCreateException {
        Ticket ticket = ticketManager.create(movieTimeNo1.toString(), clientNo1.getClientID(), movieNo1.getMovieID(), "normal");
        assertNotNull(ticket);
        assertEquals(movieTimeNo1, ticket.getMovieTime());
        assertEquals(clientNo1.getClientID(), ticket.getClient().getClientID());
        assertEquals(movieNo1.getMovieID(), ticket.getMovie().getMovieID());
        assertEquals(movieNo1.getMovieBasePrice(), ticket.getTicketFinalPrice());
    }

    @Test
    public void ticketManagerCreateTicketReducedTestPositive() throws TicketManagerCreateException {
        Ticket ticket = ticketManager.create(movieTimeNo1.toString(), clientNo1.getClientID(), movieNo1.getMovieID(), "reduced");
        assertNotNull(ticket);
        assertEquals(movieTimeNo1, ticket.getMovieTime());
        assertEquals(clientNo1.getClientID(), ticket.getClient().getClientID());
        assertEquals(movieNo1.getMovieID(), ticket.getMovie().getMovieID());
        assertEquals(movieNo1.getMovieBasePrice() * 0.75, ticket.getTicketFinalPrice());
    }

    @Test
    public void ticketManagerCreateTicketWithNullClientTestNegative() {
        assertThrows(TicketManagerCreateException.class, () -> ticketManager.create(movieTimeNo1.toString(), null, movieNo1.getMovieID(), "normal"));
    }

    @Test
    public void ticketManagerCreateTicketWithNullMovieTestNegative() {
        assertThrows(TicketManagerCreateException.class, () -> ticketManager.create(movieTimeNo1.toString(), clientNo1.getClientID(), null, "normal"));
    }

    // Read tests

    @Test
    public void ticketManagerFindTicketByIDTestPositive() throws TicketManagerReadException {
        Ticket ticket = ticketManager.findByUUID(ticketNo1.getTicketID());
        assertNotNull(ticket);
        assertEquals(ticketNo1, ticket);
    }

    @Test
    public void ticketManagerFindTicketByIDThatIsNotInTheDatabaseTestPositive() throws TicketCreateException {
        Ticket ticket = new Ticket(UUID.randomUUID(), movieTimeNo1, clientNo1, movieNo1, TicketType.NORMAL);
        assertNotNull(ticket);
        assertThrows(TicketManagerReadException.class, () -> ticketManager.findByUUID(ticket.getTicketID()));
    }

    @Test
    public void ticketManagerFindAllTicketsTestPositive() throws TicketManagerReadException {
        List<Ticket> listOfTickets = ticketManager.findAll();
        assertNotNull(listOfTickets);
        assertFalse(listOfTickets.isEmpty());
        assertEquals(6, listOfTickets.size());
    }

    // Update tests

    @Test
    public void ticketManagerUpdateTicketTestPositive() throws TicketManagerUpdateException, TicketManagerReadException {
        LocalDateTime newMovieTime = LocalDateTime.now().plusDays(4).plusHours(12).plusMinutes(30).truncatedTo(ChronoUnit.SECONDS);
        ticketNo1.setMovieTime(newMovieTime);
        ticketManager.update(ticketNo1);
        Ticket foundTicket = ticketManager.findByUUID(ticketNo1.getTicketID());
        assertEquals(newMovieTime, foundTicket.getMovieTime());
    }

    @Test
    public void ticketManagerUpdateTicketThatIsNotInTheDatabaseTestNegative() throws TicketCreateException {
        Ticket ticket = new Ticket(UUID.randomUUID(), movieTimeNo1, clientNo1, movieNo1, TicketType.NORMAL);
        assertNotNull(ticket);
        assertThrows(TicketManagerUpdateException.class, () -> ticketManager.update(ticket));
    }

    // Delete tests

    @Test
    public void ticketManagerDeleteTicketTestPositive() throws TicketManagerReadException, TicketManagerDeleteException {
        UUID removedTicketUUID = ticketNo1.getTicketID();
        Ticket foundTicket = ticketManager.findByUUID(removedTicketUUID);
        assertNotNull(foundTicket);
        assertEquals(ticketNo1, foundTicket);
        ticketManager.delete(removedTicketUUID);
        assertThrows(TicketManagerReadException.class, () -> ticketManager.findByUUID(removedTicketUUID));
    }

    @Test
    public void ticketManagerDeleteTicketThatIsNotInTheDatabaseTestNegative() throws TicketCreateException {
        Ticket ticket = new Ticket(UUID.randomUUID(), movieTimeNo1, clientNo1, movieNo1, TicketType.NORMAL);
        assertNotNull(ticket);
        assertThrows(TicketManagerDeleteException.class, () -> ticketManager.delete(ticket.getTicketID()));
    }

    // Other tests

    @Test
    public void movieManagerDeleteMovieThatIsUsedInTicketTestNegative() {
        assertThrows(MovieManagerDeleteException.class, () -> movieManager.delete(movieNo1.getMovieID()));
    }

    @Test
    public void movieManagerGetAllTicketForCertainMovieTestPositive() {
        List<Ticket> listOfTicketsForMovieNo1 = movieManager.getListOfTicketsForCertainMovie(movieNo1.getMovieID());
        assertNotNull(listOfTicketsForMovieNo1);
        assertFalse(listOfTicketsForMovieNo1.isEmpty());
        assertEquals(3, listOfTicketsForMovieNo1.size());
    }

    @Test
    public void clientManagerGetAllTicketForCertainClientTestPositive() throws ClientManagerReadException {
        List<Ticket> listOfTicketsForClientNo1 = clientManager.getTicketsForClient(clientNo1.getClientID());
        assertNotNull(listOfTicketsForClientNo1);
        assertFalse(listOfTicketsForClientNo1.isEmpty());
        assertEquals(2, listOfTicketsForClientNo1.size());
    }

    @Test
    public void clientManagerGetAllTicketForCertainClientButUsingAdminIDTestPositive() {
        assertThrows(ClientManagerReadException.class, () -> clientManager.getTicketsForClient(adminNo1.getClientID()));
    }

    @Test
    public void clientManagerGetAllTicketForCertainClientButUsingStaffIDTestPositive() {
        assertThrows(ClientManagerReadException.class, () -> clientManager.getTicketsForClient(staffNo1.getClientID()));
    }

    @Test
    public void adminManagerGetAllTicketForCertainAdminTestPositive() throws AdminManagerReadException {
        List<Ticket> listOfTicketsForAdminNo1 = adminManager.getTicketsForClient(adminNo1.getClientID());
        assertNotNull(listOfTicketsForAdminNo1);
        assertFalse(listOfTicketsForAdminNo1.isEmpty());
        assertEquals(2, listOfTicketsForAdminNo1.size());
    }

    @Test
    public void adminManagerGetAllTicketForCertainAdminButUsingClientIDTestPositive() {
        assertThrows(AdminManagerReadException.class, () -> adminManager.getTicketsForClient(clientNo1.getClientID()));
    }

    @Test
    public void adminManagerGetAllTicketForCertainAdminButUsingStaffIDTestPositive() {
        assertThrows(AdminManagerReadException.class, () -> adminManager.getTicketsForClient(staffNo1.getClientID()));
    }

    @Test
    public void staffManagerGetAllTicketForCertainStaffTestPositive() throws StaffManagerReadException {
        List<Ticket> listOfTicketsForStaffNo1 = staffManager.getTicketsForClient(staffNo1.getClientID());
        assertNotNull(listOfTicketsForStaffNo1);
        assertFalse(listOfTicketsForStaffNo1.isEmpty());
        assertEquals(2, listOfTicketsForStaffNo1.size());
    }

    @Test
    public void staffManagerGetAllTicketForCertainAdminButUsingClientIDTestPositive() {
        assertThrows(StaffManagerReadException.class, () -> staffManager.getTicketsForClient(clientNo1.getClientID()));
    }

    @Test
    public void staffManagerGetAllTicketForCertainStaffButUsingAdminIDTestPositive() {
        assertThrows(StaffManagerReadException.class, () -> staffManager.getTicketsForClient(adminNo1.getClientID()));
    }
}