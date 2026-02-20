package pl.pas.gr3.cinema.services;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.pas.gr3.cinema.entity.Movie;
import pl.pas.gr3.cinema.entity.Ticket;
import pl.pas.gr3.cinema.entity.account.Admin;
import pl.pas.gr3.cinema.entity.account.Client;
import pl.pas.gr3.cinema.entity.account.Staff;
import pl.pas.gr3.cinema.repository.impl.AccountRepositoryImpl;
import pl.pas.gr3.cinema.repository.impl.MovieRepositoryImpl;
import pl.pas.gr3.cinema.repository.impl.TicketRepositoryImpl;
import pl.pas.gr3.cinema.service.impl.AdminServiceImpl;
import pl.pas.gr3.cinema.service.impl.ClientServiceImpl;
import pl.pas.gr3.cinema.service.impl.MovieServiceImpl;
import pl.pas.gr3.cinema.service.impl.StaffServiceImpl;
import pl.pas.gr3.cinema.service.impl.TicketServiceImpl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class TicketServiceTest {

    private static final String DATABASE_NAME = "test";

    private static AccountRepositoryImpl accountRepository;
    private static MovieRepositoryImpl movieRepository;
    private static TicketRepositoryImpl ticketRepository;

    private static ClientServiceImpl clientService;
    private static AdminServiceImpl adminService;
    private static StaffServiceImpl staffService;
    private static MovieServiceImpl movieService;
    private static TicketServiceImpl ticketService;

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
    static void initialize() {
        accountRepository = new AccountRepositoryImpl(DATABASE_NAME);
        movieRepository = new MovieRepositoryImpl(DATABASE_NAME);
        ticketRepository = new TicketRepositoryImpl(DATABASE_NAME);

        clientService = new ClientServiceImpl(accountRepository);
        adminService = new AdminServiceImpl(accountRepository);
        staffService = new StaffServiceImpl(accountRepository);
        movieService = new MovieServiceImpl(movieRepository);
        ticketService = new TicketServiceImpl(ticketRepository);
    }

    @BeforeEach
    void initializeSampleData() {
        try {
            clientNo1 = clientService.create("UniqueClientLoginNo1", "UniqueClientPasswordNo1");
            clientNo2 = clientService.create("UniqueClientLoginNo2", "UniqueClientPasswordNo2");

            adminNo1 = adminService.create("UniqueAdminLoginNo1", "UniqueAdminPasswordNo1");
            adminNo2 = adminService.create("UniqueAdminLoginNo2", "UniqueAdminPasswordNo2");

            staffNo1 = staffService.create("UniqueStaffLoginNo1", "UniqueStaffPasswordNo1");
            staffNo2 = staffService.create("UniqueStaffLoginNo2", "UniqueStaffPasswordNo2");

            movieNo1 = movieService.create("UniqueMovieTitleNo1", 45.00, 1, 60);
            movieNo2 = movieService.create("UniqueMovieTitleNo2", 35.50, 2, 40);

            movieTimeNo1 = LocalDateTime.now().plusHours(3).plusDays(2).truncatedTo(ChronoUnit.SECONDS);
            movieTimeNo2 = LocalDateTime.now().plusHours(8).plusDays(5).truncatedTo(ChronoUnit.SECONDS);

            ticketNo1 = ticketService.create(movieTimeNo1.toString(), clientNo1.getId(), movieNo1.getId());
            ticketNo2 = ticketService.create(movieTimeNo2.toString(), clientNo1.getId(), movieNo2.getId());

            ticketNo3 = ticketService.create(movieTimeNo1.toString(), adminNo1.getId(), movieNo1.getId());
            ticketNo4 = ticketService.create(movieTimeNo2.toString(), adminNo1.getId(), movieNo2.getId());

            ticketNo5 = ticketService.create(movieTimeNo1.toString(), staffNo1.getId(), movieNo1.getId());
            ticketNo6 = ticketService.create(movieTimeNo2.toString(), staffNo1.getId(), movieNo2.getId());
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
    }

    @AfterEach
    void destroySampleData() {
        try {
            List<Ticket> tickets = ticketService.findAll();
            tickets.forEach(ticket -> ticketService.delete(ticket.getId()));

            List<Movie> movies = movieService.findAll();
            movies.forEach(movie -> movieService.delete(movie.getId()));

            List<Client> clients = clientService.findAll();
            clients.forEach(client -> clientService.delete(client.getId()));

            List<Admin> admins = adminService.findAll();
            admins.forEach(admin -> adminService.delete(admin.getId()));

            List<Staff> staffs = staffService.findAll();
            staffs.forEach(staff -> staffService.delete(staff.getId()));
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
    }

    @AfterAll
    static void destroy() {
        accountRepository.close();
        movieRepository.close();
        ticketRepository.close();
    }

    // Constructor tests

    @Test
    void ticketServiceAllArgsConstructorTestPositive() {
        TicketServiceImpl testTicketService = new TicketServiceImpl(ticketRepository);
        assertNotNull(testTicketService);
    }
    
    // Create tests

    @Test
    void ticketServiceCreateTicketNormalTestPositive() throws TicketServiceCreateException {
        Ticket ticket = ticketService.create(movieTimeNo1.toString(), clientNo1.getId(), movieNo1.getId());
        assertNotNull(ticket);
        assertEquals(movieTimeNo1, ticket.getMovieTime());
        assertEquals(clientNo1.getId(), ticket.getUserId());
        assertEquals(movieNo1.getId(), ticket.getMovieId());
        assertEquals(movieNo1.getBasePrice(), ticket.getPrice());
    }

    @Test
    void ticketServiceCreateTicketReducedTestPositive() throws TicketServiceCreateException {
        Ticket ticket = ticketService.create(movieTimeNo1.toString(), clientNo1.getId(), movieNo1.getId());
        assertNotNull(ticket);
        assertEquals(movieTimeNo1, ticket.getMovieTime());
        assertEquals(clientNo1.getId(), ticket.getUserId());
        assertEquals(movieNo1.getId(), ticket.getMovieId());
        assertEquals(movieNo1.getBasePrice(), ticket.getPrice());
    }

    @Test
    void ticketServiceCreateTicketWithNullClientTestNegative() {
        assertThrows(TicketServiceCreateException.class, () -> ticketService.create(movieTimeNo1.toString(), null, movieNo1.getId()));
    }

    @Test
    void ticketServiceCreateTicketWithNullMovieTestNegative() {
        assertThrows(TicketServiceCreateException.class, () -> ticketService.create(movieTimeNo1.toString(), clientNo1.getId(), null));
    }

    // Read tests

    @Test
    void ticketServiceFindTicketByIDTestPositive() throws TicketServiceReadException {
        Ticket ticket = ticketService.findByUUID(ticketNo1.getId());
        assertNotNull(ticket);
        assertEquals(ticketNo1, ticket);
    }

    @Test
    void ticketServiceFindTicketByIDThatIsNotInTheDatabaseTestPositive() {
        Ticket ticket = new Ticket(UUID.randomUUID(), movieTimeNo1, movieNo1.getBasePrice(), clientNo1.getId(), movieNo1.getId());
        assertNotNull(ticket);
        assertThrows(TicketServiceTicketNotFoundException.class, () -> ticketService.findByUUID(ticket.getId()));
    }

    @Test
    void ticketServiceFindAllTicketsTestPositive() throws TicketServiceReadException {
        List<Ticket> listOfTickets = ticketService.findAll();
        assertNotNull(listOfTickets);
        assertFalse(listOfTickets.isEmpty());
        assertEquals(6, listOfTickets.size());
    }

    // Update tests

    @Test
    void ticketServiceUpdateTicketTestPositive() throws TicketServiceUpdateException, TicketServiceReadException {
        LocalDateTime newMovieTime = LocalDateTime.now().plusDays(4).plusHours(12).plusMinutes(30).truncatedTo(ChronoUnit.SECONDS);
        ticketNo1.setMovieTime(newMovieTime);
        ticketService.update(ticketNo1);
        Ticket foundTicket = ticketService.findByUUID(ticketNo1.getId());
        assertEquals(newMovieTime, foundTicket.getMovieTime());
    }

    @Test
    void ticketServiceUpdateTicketThatIsNotInTheDatabaseTestNegative() {
        Ticket ticket = new Ticket(UUID.randomUUID(), movieTimeNo1, movieNo1.getBasePrice(), clientNo1.getId(), movieNo1.getId());
        assertNotNull(ticket);
        assertThrows(TicketServiceUpdateException.class, () -> ticketService.update(ticket));
    }

    // Delete tests

    @Test
    void ticketServiceDeleteTicketTestPositive() throws TicketServiceReadException, TicketServiceDeleteException {
        UUID removedTicketUUID = ticketNo1.getId();
        Ticket foundTicket = ticketService.findByUUID(removedTicketUUID);
        assertNotNull(foundTicket);
        assertEquals(ticketNo1, foundTicket);
        ticketService.delete(removedTicketUUID);
        assertThrows(TicketServiceReadException.class, () -> ticketService.findByUUID(removedTicketUUID));
    }

    @Test
    void ticketServiceDeleteTicketThatIsNotInTheDatabaseTestNegative() {
        Ticket ticket = new Ticket(UUID.randomUUID(), movieTimeNo1, movieNo1.getBasePrice(), clientNo1.getId(), movieNo1.getId());
        assertNotNull(ticket);
        assertThrows(TicketServiceDeleteException.class, () -> ticketService.delete(ticket.getId()));
    }

    // Other tests

    @Test
    void movieServiceDeleteMovieThatIsUsedInTicketTestNegative() {
        assertThrows(MovieServiceDeleteException.class, () -> movieService.delete(movieNo1.getId()));
    }

    @Test
    void movieServiceGetAllTicketForCertainMovieTestPositive() {
        List<Ticket> listOfTicketsForMovieNo1 = movieService.getListOfTicketsForCertainMovie(movieNo1.getId());
        assertNotNull(listOfTicketsForMovieNo1);
        assertFalse(listOfTicketsForMovieNo1.isEmpty());
        assertEquals(3, listOfTicketsForMovieNo1.size());
    }

    @Test
    void clientServiceGetAllTicketForCertainClientTestPositive() throws ClientServiceReadException {
        List<Ticket> listOfTicketsForClientNo1 = clientService.getTicketsForClient(clientNo1.getId());
        assertNotNull(listOfTicketsForClientNo1);
        assertFalse(listOfTicketsForClientNo1.isEmpty());
        assertEquals(2, listOfTicketsForClientNo1.size());
    }

    @Test
    void clientServiceGetAllTicketForCertainClientButUsingAdminIDTestPositive() {
        assertThrows(ClientServiceReadException.class, () -> clientService.getTicketsForClient(adminNo1.getId()));
    }

    @Test
    void clientServiceGetAllTicketForCertainClientButUsingStaffIDTestPositive() {
        assertThrows(ClientServiceReadException.class, () -> clientService.getTicketsForClient(staffNo1.getId()));
    }

    @Test
    void adminServiceGetAllTicketForCertainAdminTestPositive() throws AdminServiceReadException {
        List<Ticket> listOfTicketsForAdminNo1 = adminService.getTicketsForClient(adminNo1.getId());
        assertNotNull(listOfTicketsForAdminNo1);
        assertFalse(listOfTicketsForAdminNo1.isEmpty());
        assertEquals(2, listOfTicketsForAdminNo1.size());
    }

    @Test
    void adminServiceGetAllTicketForCertainAdminButUsingClientIDTestPositive() {
        assertThrows(AdminServiceReadException.class, () -> adminService.getTicketsForClient(clientNo1.getId()));
    }

    @Test
    void adminServiceGetAllTicketForCertainAdminButUsingStaffIDTestPositive() {
        assertThrows(AdminServiceReadException.class, () -> adminService.getTicketsForClient(staffNo1.getId()));
    }

    @Test
    void staffServiceGetAllTicketForCertainStaffTestPositive() throws StaffServiceReadException {
        List<Ticket> listOfTicketsForStaffNo1 = staffService.getTicketsForClient(staffNo1.getId());
        assertNotNull(listOfTicketsForStaffNo1);
        assertFalse(listOfTicketsForStaffNo1.isEmpty());
        assertEquals(2, listOfTicketsForStaffNo1.size());
    }

    @Test
    void staffServiceGetAllTicketForCertainAdminButUsingClientIDTestPositive() {
        assertThrows(StaffServiceReadException.class, () -> staffService.getTicketsForClient(clientNo1.getId()));
    }

    @Test
    void staffServiceGetAllTicketForCertainStaffButUsingAdminIDTestPositive() {
        assertThrows(StaffServiceReadException.class, () -> staffService.getTicketsForClient(adminNo1.getId()));
    }
}