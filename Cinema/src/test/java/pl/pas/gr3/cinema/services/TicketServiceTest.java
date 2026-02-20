package pl.pas.gr3.cinema.services;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.util.ReflectionTestUtils;
import pl.pas.gr3.cinema.entity.Movie;
import pl.pas.gr3.cinema.entity.Ticket;
import pl.pas.gr3.cinema.entity.account.Admin;
import pl.pas.gr3.cinema.entity.account.Client;
import pl.pas.gr3.cinema.entity.account.Staff;
import pl.pas.gr3.cinema.exception.bad_request.MovieDeleteException;
import pl.pas.gr3.cinema.exception.bad_request.TicketCreateException;
import pl.pas.gr3.cinema.exception.bad_request.TicketDeleteException;
import pl.pas.gr3.cinema.exception.bad_request.TicketUpdateException;
import pl.pas.gr3.cinema.exception.not_found.AccountNotFoundException;
import pl.pas.gr3.cinema.exception.not_found.TicketNotFoundException;
import pl.pas.gr3.cinema.mapper.AccountMapper;
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

    private AccountMapper accountMapper;

    @BeforeAll
    static void initialize() {
        accountRepository = new AccountRepositoryImpl(DATABASE_NAME);
        movieRepository = new MovieRepositoryImpl(DATABASE_NAME);
        ticketRepository = new TicketRepositoryImpl(DATABASE_NAME);

        clientService = new ClientServiceImpl(accountRepository);
        adminService = new AdminServiceImpl(accountRepository);
        staffService = new StaffServiceImpl(accountRepository);
        movieService = new MovieServiceImpl(movieRepository);
        ticketService = new TicketServiceImpl(accountRepository, ticketRepository);
    }

    @BeforeEach
    void initializeSampleData() {
        accountMapper = Mappers.getMapper(AccountMapper.class);
        ReflectionTestUtils.setField(accountRepository, "accountMapper", accountMapper);

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
            tickets.forEach(ticket -> ticketRepository.delete(ticket.getId()));

            List<Movie> movies = movieService.findAll();
            movies.forEach(movie -> movieRepository.delete(movie.getId()));

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
        TicketServiceImpl testTicketService = new TicketServiceImpl(accountRepository, ticketRepository);
        assertNotNull(testTicketService);
    }
    
    // Create tests

    @Test
    void ticketServiceCreateTicketNormalTestPositive() {
        Ticket ticket = ticketService.create(movieTimeNo1.toString(), clientNo1.getId(), movieNo1.getId());
        assertNotNull(ticket);
        assertEquals(movieTimeNo1, ticket.getMovieTime());
        assertEquals(clientNo1.getId(), ticket.getUserId());
        assertEquals(movieNo1.getId(), ticket.getMovieId());
        assertEquals(movieNo1.getBasePrice(), ticket.getPrice());
    }

    @Test
    void ticketServiceCreateTicketReducedTestPositive() {
        Ticket ticket = ticketService.create(movieTimeNo1.toString(), clientNo1.getId(), movieNo1.getId());
        assertNotNull(ticket);
        assertEquals(movieTimeNo1, ticket.getMovieTime());
        assertEquals(clientNo1.getId(), ticket.getUserId());
        assertEquals(movieNo1.getId(), ticket.getMovieId());
        assertEquals(movieNo1.getBasePrice(), ticket.getPrice());
    }

    @Test
    void ticketServiceCreateTicketWithNullClientTestNegative() {
        assertThrows(TicketCreateException.class, () -> ticketService.create(movieTimeNo1.toString(), null, movieNo1.getId()));
    }

    @Test
    void ticketServiceCreateTicketWithNullMovieTestNegative() {
        assertThrows(TicketCreateException.class, () -> ticketService.create(movieTimeNo1.toString(), clientNo1.getId(), null));
    }

    // Read tests

    @Test
    void ticketServiceFindTicketByIDTestPositive() {
        Ticket ticket = ticketService.findByUUID(ticketNo1.getId());
        assertNotNull(ticket);
        assertEquals(ticketNo1, ticket);
    }

    @Test
    void ticketServiceFindTicketByIDThatIsNotInTheDatabaseTestPositive() {
        Ticket ticket = new Ticket(UUID.randomUUID(), movieTimeNo1, movieNo1.getBasePrice(), clientNo1.getId(), movieNo1.getId());
        assertNotNull(ticket);
        assertThrows(TicketNotFoundException.class, () -> ticketService.findByUUID(ticket.getId()));
    }

    @Test
    void ticketServiceFindAllTicketsTestPositive() {
        List<Ticket> listOfTickets = ticketService.findAll();
        assertNotNull(listOfTickets);
        assertFalse(listOfTickets.isEmpty());
        assertEquals(6, listOfTickets.size());
    }

    // Update tests

    @Test
    void ticketServiceUpdateTicketTestPositive() {
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
        assertThrows(TicketUpdateException.class, () -> ticketService.update(ticket));
    }

    // Delete tests

    @Test
    void ticketServiceDeleteTicketTestPositive() {
        UUID removedTicketUUID = ticketNo1.getId();
        Ticket foundTicket = ticketService.findByUUID(removedTicketUUID);
        assertNotNull(foundTicket);
        assertEquals(ticketNo1, foundTicket);
        ticketService.delete(removedTicketUUID);
        assertThrows(TicketNotFoundException.class, () -> ticketService.findByUUID(removedTicketUUID));
    }

    @Test
    void ticketServiceDeleteTicketThatIsNotInTheDatabaseTestNegative() {
        Ticket ticket = new Ticket(UUID.randomUUID(), movieTimeNo1, movieNo1.getBasePrice(), clientNo1.getId(), movieNo1.getId());
        assertNotNull(ticket);
        assertThrows(TicketDeleteException.class, () -> ticketService.delete(ticket.getId()));
    }

    // Other tests

    @Test
    void movieServiceDeleteMovieThatIsUsedInTicketTestNegative() {
        assertThrows(MovieDeleteException.class, () -> movieService.delete(movieNo1.getId()));
    }

    @Test
    void movieServiceGetAllTicketForCertainMovieTestPositive() {
        List<Ticket> listOfTicketsForMovieNo1 = movieService.getListOfTicketsForCertainMovie(movieNo1.getId());
        assertNotNull(listOfTicketsForMovieNo1);
        assertFalse(listOfTicketsForMovieNo1.isEmpty());
        assertEquals(3, listOfTicketsForMovieNo1.size());
    }

    @Test
    void clientServiceGetAllTicketForCertainClientTestPositive() {
        List<Ticket> listOfTicketsForClientNo1 = clientService.getTicketsForClient(clientNo1.getId());
        assertNotNull(listOfTicketsForClientNo1);
        assertFalse(listOfTicketsForClientNo1.isEmpty());
        assertEquals(2, listOfTicketsForClientNo1.size());
    }

    @Test
    void clientServiceGetAllTicketForCertainClientButUsingAdminIDTestPositive() {
        assertThrows(AccountNotFoundException.class, () -> clientService.getTicketsForClient(adminNo1.getId()));
    }

    @Test
    void clientServiceGetAllTicketForCertainClientButUsingStaffIDTestPositive() {
        assertThrows(AccountNotFoundException.class, () -> clientService.getTicketsForClient(staffNo1.getId()));
    }

    @Test
    void adminServiceGetAllTicketForCertainAdminTestPositive() {
        List<Ticket> listOfTicketsForAdminNo1 = adminService.getTicketsForClient(adminNo1.getId());
        assertNotNull(listOfTicketsForAdminNo1);
        assertFalse(listOfTicketsForAdminNo1.isEmpty());
        assertEquals(2, listOfTicketsForAdminNo1.size());
    }

    @Test
    void adminServiceGetAllTicketForCertainAdminButUsingClientIDTestPositive() {
        assertThrows(AccountNotFoundException.class, () -> adminService.getTicketsForClient(clientNo1.getId()));
    }

    @Test
    void adminServiceGetAllTicketForCertainAdminButUsingStaffIDTestPositive() {
        assertThrows(AccountNotFoundException.class, () -> adminService.getTicketsForClient(staffNo1.getId()));
    }

    @Test
    void staffServiceGetAllTicketForCertainStaffTestPositive() {
        List<Ticket> listOfTicketsForStaffNo1 = staffService.getTicketsForClient(staffNo1.getId());
        assertNotNull(listOfTicketsForStaffNo1);
        assertFalse(listOfTicketsForStaffNo1.isEmpty());
        assertEquals(2, listOfTicketsForStaffNo1.size());
    }

    @Test
    void staffServiceGetAllTicketForCertainAdminButUsingClientIDTestPositive() {
        assertThrows(AccountNotFoundException.class, () -> staffService.getTicketsForClient(clientNo1.getId()));
    }

    @Test
    void staffServiceGetAllTicketForCertainStaffButUsingAdminIDTestPositive() {
        assertThrows(AccountNotFoundException.class, () -> staffService.getTicketsForClient(adminNo1.getId()));
    }
}