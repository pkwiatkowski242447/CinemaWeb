package pl.pas.gr3.cinema.services;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.util.ReflectionTestUtils;
import pl.pas.gr3.cinema.exception.bad_request.AccountActivationException;
import pl.pas.gr3.cinema.exception.bad_request.AccountCreateException;
import pl.pas.gr3.cinema.exception.bad_request.AccountDeleteException;
import pl.pas.gr3.cinema.exception.bad_request.AccountUpdateException;
import pl.pas.gr3.cinema.exception.not_found.AccountNotFoundException;
import pl.pas.gr3.cinema.mapper.AccountMapper;
import pl.pas.gr3.cinema.service.impl.ClientServiceImpl;
import pl.pas.gr3.cinema.entity.account.Client;
import pl.pas.gr3.cinema.repository.impl.AccountRepositoryImpl;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class ClientServiceTest {

    private static final String DATABASE_NAME = "test";
    private static AccountRepositoryImpl accountRepository;
    private static ClientServiceImpl clientService;

    private Client clientNo1;
    private Client clientNo2;

    private AccountMapper accountMapper;

    @BeforeAll
    static void initialize() {
        accountRepository = new AccountRepositoryImpl(DATABASE_NAME);
        clientService = new ClientServiceImpl(accountRepository);
    }

    @BeforeEach
    void initializeSampleData() {
        accountMapper = Mappers.getMapper(AccountMapper.class);
        ReflectionTestUtils.setField(accountRepository, "accountMapper", accountMapper);

        this.clearTestData();
        try {
            clientNo1 = clientService.create("UniqueClientLoginNo1", "UniqueClientPasswordNo1");
            clientNo2 = clientService.create("UniqueClientLoginNo2", "UniqueClientPasswordNo2");
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
    }

    @AfterEach
    void destroySampleData() {
        this.clearTestData();
    }

    private void clearTestData() {
        try {
            List<Client> clients = clientService.findAll();
            clients.forEach(client -> clientService.delete(client.getId()));
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
    }

    @AfterAll
    static void destroy() {
        accountRepository.close();
    }

    // Constructor tests

    @Test
    void clientServiceAllArgsConstructorTestPositive() {
        ClientServiceImpl testClientService = new ClientServiceImpl(accountRepository);
        assertNotNull(testClientService);
    }

    // Create tests
    
    @Test
    void clientServiceCreateClientTestPositive() {
        String clientLogin = "SomeOtherLoginNo1";
        String clientPassword = "SomeOtherPasswordNo1";
        Client client = clientService.create(clientLogin, clientPassword);
        assertNotNull(client);
        assertEquals(clientLogin, client.getLogin());
        assertEquals(clientPassword, client.getPassword());
    }

    @Test
    void clientServiceCreateClientWithNullLoginThatTestNegative() {
        String clientLogin = null;
        String clientPassword = "SomeOtherPasswordNo1";
        assertThrows(AccountCreateException.class, () -> clientService.create(clientLogin, clientPassword));
    }

    @Test
    void clientServiceCreateClientWithEmptyLoginThatTestNegative() {
        String clientLogin = "";
        String clientPassword = "SomeOtherPasswordNo1";
        assertThrows(AccountCreateException.class, () -> clientService.create(clientLogin, clientPassword));
    }

    @Test
    void clientServiceCreateClientWithLoginTooShortThatTestNegative() {
        String clientLogin = "ddddfdd";
        String clientPassword = "SomeOtherPasswordNo1";
        assertThrows(AccountCreateException.class, () -> clientService.create(clientLogin, clientPassword));
    }

    @Test
    void clientServiceCreateClientWithLoginTooLongThatTestNegative() {
        String clientLogin = "ddddfddddfddddfddddfd";
        String clientPassword = "SomeOtherPasswordNo1";
        assertThrows(AccountCreateException.class, () -> clientService.create(clientLogin, clientPassword));
    }

    @Test
    void clientServiceCreateClientWithLoginLengthEqualTo8ThatTestPositive() {
        String clientLogin = "ddddfddd";
        String clientPassword = "SomeOtherPasswordNo1";
        Client client = clientService.create(clientLogin, clientPassword);
        assertNotNull(client);
        assertEquals(clientLogin, client.getLogin());
        assertEquals(clientPassword, client.getPassword());
    }

    @Test
    void clientServiceCreateClientWithLoginLengthEqualTo20ThatTestPositive() {
        String clientLogin = "ddddfddddfddddfddddf";
        String clientPassword = "SomeOtherPasswordNo1";
        Client client = clientService.create(clientLogin, clientPassword);
        assertNotNull(client);
        assertEquals(clientLogin, client.getLogin());
        assertEquals(clientPassword, client.getPassword());
    }

    @Test
    void clientServiceCreateClientWithLoginThatDoesNotMeetRegExTestNegative() {
        String clientLogin = "Some Invalid Login";
        String clientPassword = "SomeOtherPasswordNo1";
        assertThrows(AccountCreateException.class, () -> clientService.create(clientLogin, clientPassword));
    }

    // Read tests

    @Test
    void clientServiceFindClientByIDTestPositive() {
        Client foundClient = clientService.findByUUID(clientNo1.getId());
        assertNotNull(foundClient);
        assertEquals(clientNo1, foundClient);
    }

    @Test
    void clientServiceFindClientByIDThatIsNotInTheDatabaseTestNegative() {
        Client client = new Client(UUID.randomUUID(), "SomeOtherLoginNo1", "SomeOtherPasswordNo1");
        assertNotNull(client);
        assertThrows(AccountNotFoundException.class, () -> clientService.findByUUID(client.getId()));
    }

    @Test
    void clientServiceFindClientByLoginTestPositive() {
        Client foundClient = clientService.findByLogin(clientNo1.getLogin());
        assertNotNull(foundClient);
        assertEquals(clientNo1, foundClient);
    }

    @Test
    void clientServiceFindClientByLoginThatIsNotInTheDatabaseTestNegative() {
        Client client = new Client(UUID.randomUUID(), "SomeOtherLoginNo1", "SomeOtherPasswordNo1");
        assertNotNull(client);
        assertThrows(AccountNotFoundException.class, () -> clientService.findByLogin(client.getLogin()));
    }

    @Test
    void clientServiceFindAllClientsMatchingLoginTestPositive() {
        clientService.create("NewClientLogin", "NewClientPassword");
        List<Client> listOfClients = clientService.findAllMatchingLogin("New");
        assertNotNull(listOfClients);
        assertFalse(listOfClients.isEmpty());
        assertEquals(1, listOfClients.size());
    }

    @Test
    void clientServiceFindAllClientsTestPositive() {
        List<Client> listOfClients = clientService.findAll();
        assertNotNull(listOfClients);
        assertFalse(listOfClients.isEmpty());
        assertEquals(2, listOfClients.size());
    }

    // Update tests

    @Test
    void clientServiceUpdateClientTestPositive() {
        String clientLoginBefore = clientNo1.getLogin();
        String clientPasswordBefore = clientNo1.getPassword();
        String newClientLogin = "OtherNewLoginNo1";
        String newClientPassword = "OtherNewPasswordNo1";
        clientNo1.setLogin(newClientLogin);
        clientNo1.setPassword(newClientPassword);
        clientService.update(clientNo1);
        Client foundClient = clientService.findByUUID(clientNo1.getId());
        String clientLoginAfter =  foundClient.getLogin();
        String clientPasswordAfter = foundClient.getPassword();
        assertNotNull(clientLoginAfter);
        assertNotNull(clientPasswordAfter);
        assertEquals(newClientLogin, clientLoginAfter);
        assertEquals(newClientPassword, clientPasswordAfter);
        assertNotEquals(clientLoginBefore, clientLoginAfter);
        assertNotEquals(clientPasswordBefore, clientPasswordAfter);
    }

    @Test
    void clientServiceUpdateClientWithNullLoginTestNegative() {
        String clientLogin = null;
        String clientPassword = "SomeOtherPasswordNo2";
        clientNo1.setLogin(clientLogin);
        clientNo1.setPassword(clientPassword);
        assertThrows(AccountUpdateException.class, () -> clientService.update(clientNo1));
    }

    @Test
    void clientServiceUpdateClientWithEmptyLoginTestNegative() {
        String clientLogin = "";
        String clientPassword = "SomeOtherPasswordNo2";
        clientNo1.setLogin(clientLogin);
        clientNo1.setPassword(clientPassword);
        assertThrows(AccountUpdateException.class, () -> clientService.update(clientNo1));
    }

    @Test
    void clientServiceUpdateClientWithLoginTooShortTestNegative() {
        String clientLogin = "ddddfdd";
        String clientPassword = "SomeOtherPasswordNo2";
        clientNo1.setLogin(clientLogin);
        clientNo1.setPassword(clientPassword);
        assertThrows(AccountUpdateException.class, () -> clientService.update(clientNo1));
    }

    @Test
    void clientServiceUpdateClientWithLoginTooLongTestNegative() {
        String clientLogin = "ddddfddddfddddfddddfd";
        String clientPassword = "SomeOtherPasswordNo2";
        clientNo1.setLogin(clientLogin);
        clientNo1.setPassword(clientPassword);
        assertThrows(AccountUpdateException.class, () -> clientService.update(clientNo1));
    }

    @Test
    void clientServiceUpdateClientWithLoginLengthEqualTo8TestPositive() {
        String clientLogin = "ddddfddd";
        String clientPassword = "SomeOtherPasswordNo2";
        clientNo1.setLogin(clientLogin);
        clientNo1.setPassword(clientPassword);
        assertDoesNotThrow(() -> clientService.update(clientNo1));
        Client foundClient = clientService.findByUUID(clientNo1.getId());
        assertEquals(clientLogin, foundClient.getLogin());
        assertEquals(clientPassword, foundClient.getPassword());
    }

    @Test
    void clientServiceUpdateClientWithLoginLengthEqualTo20TestPositive() {
        String clientLogin = "ddddfddddfddddfddddf";
        String clientPassword = "SomeOtherPasswordNo2";
        clientNo1.setLogin(clientLogin);
        clientNo1.setPassword(clientPassword);
        assertDoesNotThrow(() -> clientService.update(clientNo1));
        Client foundClient = clientService.findByUUID(clientNo1.getId());
        assertEquals(clientLogin, foundClient.getLogin());
        assertEquals(clientPassword, foundClient.getPassword());
    }

    @Test
    void clientServiceUpdateClientWithLoginThatViolatesRegExTestNegative() {
        String clientLogin = "Some Invalid Login";
        String clientPassword = "SomeOtherPasswordNo2";
        clientNo1.setLogin(clientLogin);
        clientNo1.setPassword(clientPassword);
        assertThrows(AccountUpdateException.class, () -> clientService.update(clientNo1));
    }

    // Delete tests

    @Test
    void clientServiceDeleteClientTestPositive() {
        UUID removedClientUUID = clientNo1.getId();
        Client foundClient = clientService.findByUUID(removedClientUUID);
        assertNotNull(foundClient);
        assertEquals(clientNo1, foundClient);
        clientService.delete(removedClientUUID);
        assertThrows(AccountNotFoundException.class, () -> clientService.findByUUID(removedClientUUID));
    }

    @Test
    void clientServiceDeleteClientThatIsNotInTheDatabaseTestNegative() {
        Client client = new Client(UUID.randomUUID(), "SomeOtherClientLoginNo3", "SomeOtherClientPasswordNo3");
        assertNotNull(client);
        assertThrows(AccountNotFoundException.class, () -> clientService.delete(client.getId()));
    }

    // Activate tests

    @Test
    void clientServiceActivateClientTestPositive() {
        clientService.deactivate(clientNo1.getId());

        Client foundClient = clientService.findByUUID(clientNo1.getId());
        assertNotNull(foundClient);
        assertFalse(foundClient.isActive());
        clientService.activate(clientNo1.getId());
        foundClient = clientService.findByUUID(clientNo1.getId());
        assertNotNull(foundClient);
        assertTrue(foundClient.isActive());
    }

    @Test
    void clientServiceActivateClientThatIsNotInTheDatabaseTestNegative() {
        Client client = new Client(UUID.randomUUID(), "SomeOtherClientLoginNo3", "SomeOtherClientPasswordNo3");
        assertNotNull(client);
        assertThrows(AccountNotFoundException.class, () -> clientService.activate(client.getId()));
    }

    // Deactivate tests

    @Test
    void clientServiceDeactivateClientTestPositive() {
        Client foundClient = clientService.findByUUID(clientNo1.getId());
        assertNotNull(foundClient);
        assertEquals(clientNo1, foundClient);
        assertTrue(foundClient.isActive());
        clientService.deactivate(clientNo1.getId());
        foundClient = clientService.findByUUID(clientNo1.getId());
        assertNotNull(foundClient);
        assertFalse(foundClient.isActive());
    }

    @Test
    void clientServiceDeactivateClientThatIsNotInTheDatabaseTestNegative() {
        Client client = new Client(UUID.randomUUID(), "SomeOtherClientLoginNo3", "SomeOtherClientPasswordNo3");
        assertNotNull(client);
        assertThrows(AccountNotFoundException.class, () -> clientService.deactivate(client.getId()));
    }
}
