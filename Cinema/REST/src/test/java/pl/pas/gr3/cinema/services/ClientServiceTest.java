package pl.pas.gr3.cinema.services;

import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pas.gr3.cinema.exceptions.services.crud.client.*;
import pl.pas.gr3.cinema.services.implementations.ClientService;
import pl.pas.gr3.cinema.model.users.Client;
import pl.pas.gr3.cinema.repositories.implementations.ClientRepository;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ClientServiceTest {

    private final static Logger logger = LoggerFactory.getLogger(ClientServiceTest.class);

    private final static String databaseName = "test";
    private static ClientRepository clientRepository;
    private static ClientService clientService;

    private Client clientNo1;
    private Client clientNo2;

    @BeforeAll
    public static void initialize() {
        clientRepository = new ClientRepository(databaseName);
        clientService = new ClientService(clientRepository);
    }

    @BeforeEach
    public void initializeSampleData() {
        this.clearTestData();
        try {
            clientNo1 = clientService.create("UniqueClientLoginNo1", "UniqueClientPasswordNo1");
            clientNo2 = clientService.create("UniqueClientLoginNo2", "UniqueClientPasswordNo2");
        } catch (ClientServiceCreateException exception) {
            logger.error(exception.getMessage());
        }
    }

    @AfterEach
    public void destroySampleData() {
        this.clearTestData();
    }

    private void clearTestData() {
        try {
            List<Client> listOfClients = clientService.findAll();
            for (Client client : listOfClients) {
                clientService.delete(client.getClientID());
            }
        } catch (ClientServiceReadException | ClientServiceDeleteException exception) {
            logger.error(exception.getMessage());
        }
    }

    @AfterAll
    public static void destroy() {
        clientRepository.close();
    }

    // Create tests

    @Test
    public void clientManagerCreateClientTestPositive() throws ClientServiceCreateException {
        String clientLogin = "SomeOtherLoginNo1";
        String clientPassword = "SomeOtherPasswordNo1";
        Client client = clientService.create(clientLogin, clientPassword);
        assertNotNull(client);
        assertEquals(clientLogin, client.getClientLogin());
        assertEquals(clientPassword, client.getClientPassword());
    }

    @Test
    public void clientManagerCreateClientWithNullLoginThatTestNegative() {
        String clientLogin = null;
        String clientPassword = "SomeOtherPasswordNo1";
        assertThrows(ClientServiceCreateException.class, () -> clientService.create(clientLogin, clientPassword));
    }

    @Test
    public void clientManagerCreateClientWithEmptyLoginThatTestNegative() {
        String clientLogin = "";
        String clientPassword = "SomeOtherPasswordNo1";
        assertThrows(ClientServiceCreateException.class, () -> clientService.create(clientLogin, clientPassword));
    }

    @Test
    public void clientManagerCreateClientWithLoginTooShortThatTestNegative() {
        String clientLogin = "ddddfdd";
        String clientPassword = "SomeOtherPasswordNo1";
        assertThrows(ClientServiceCreateException.class, () -> clientService.create(clientLogin, clientPassword));
    }

    @Test
    public void clientManagerCreateClientWithLoginTooLongThatTestNegative() {
        String clientLogin = "ddddfddddfddddfddddfd";
        String clientPassword = "SomeOtherPasswordNo1";
        assertThrows(ClientServiceCreateException.class, () -> clientService.create(clientLogin, clientPassword));
    }

    @Test
    public void clientManagerCreateClientWithLoginLengthEqualTo8ThatTestPositive() throws ClientServiceCreateException {
        String clientLogin = "ddddfddd";
        String clientPassword = "SomeOtherPasswordNo1";
        Client client = clientService.create(clientLogin, clientPassword);
        assertNotNull(client);
        assertEquals(clientLogin, client.getClientLogin());
        assertEquals(clientPassword, client.getClientPassword());
    }

    @Test
    public void clientManagerCreateClientWithLoginLengthEqualTo20ThatTestPositive() throws ClientServiceCreateException {
        String clientLogin = "ddddfddddfddddfddddf";
        String clientPassword = "SomeOtherPasswordNo1";
        Client client = clientService.create(clientLogin, clientPassword);
        assertNotNull(client);
        assertEquals(clientLogin, client.getClientLogin());
        assertEquals(clientPassword, client.getClientPassword());
    }

    @Test
    public void clientManagerCreateClientWithLoginThatDoesNotMeetRegExTestNegative() {
        String clientLogin = "Some Invalid Login";
        String clientPassword = "SomeOtherPasswordNo1";
        assertThrows(ClientServiceCreateException.class, () -> clientService.create(clientLogin, clientPassword));
    }

    @Test
    public void clientManagerCreateClientWithLoginThatIsAlreadyInTheDatabaseTestNegative() {
        String clientLogin = clientNo1.getClientLogin();
        String clientPassword = "SomeOtherPasswordNo1";
        assertThrows(ClientServiceCreateException.class, () -> clientService.create(clientLogin, clientPassword));
    }

    @Test
    public void clientManagerCreateClientWithNullPasswordThatTestNegative() {
        String clientLogin = "SomeOtherLoginNo1";
        String clientPassword = null;
        assertThrows(ClientServiceCreateException.class, () -> clientService.create(clientLogin, clientPassword));
    }

    @Test
    public void clientManagerCreateClientWithEmptyPasswordThatTestNegative() {
        String clientLogin = "SomeOtherLoginNo1";
        String clientPassword = "";
        assertThrows(ClientServiceCreateException.class, () -> clientService.create(clientLogin, clientPassword));
    }

    @Test
    public void clientManagerCreateClientWithPasswordTooShortThatTestNegative() {
        String clientLogin = "SomeOtherLoginNo1";
        String clientPassword = "ddddfdd";
        assertThrows(ClientServiceCreateException.class, () -> clientService.create(clientLogin, clientPassword));
    }

    @Test
    public void clientManagerCreateClientWithPasswordTooLongThatTestNegative() {
        String clientLogin = "SomeOtherLoginNo1";
        String clientPassword = "ddddfddddfddddfddddfddddfddddfddddfddddfd";
        assertThrows(ClientServiceCreateException.class, () -> clientService.create(clientLogin, clientPassword));
    }

    @Test
    public void clientManagerCreateClientWithPasswordLengthEqualTo8ThatTestPositive() throws ClientServiceCreateException {
        String clientLogin = "SomeOtherLoginNo1";
        String clientPassword = "ddddfddd";
        Client client = clientService.create(clientLogin, clientPassword);
        assertNotNull(client);
        assertEquals(clientLogin, client.getClientLogin());
        assertEquals(clientPassword, client.getClientPassword());
    }

    @Test
    public void clientManagerCreateClientWithPasswordLengthEqualTo20ThatTestPositive() throws ClientServiceCreateException {
        String clientLogin = "SomeOtherLoginNo1";
        String clientPassword = "ddddfddddfddddfddddf";
        Client client = clientService.create(clientLogin, clientPassword);
        assertNotNull(client);
        assertEquals(clientLogin, client.getClientLogin());
        assertEquals(clientPassword, client.getClientPassword());
    }

    @Test
    public void clientManagerCreateClientWithPasswordThatDoesNotMeetRegExTestNegative() {
        String clientLogin = "SomeOtherLoginNo1";
        String clientPassword = "Some Invalid Password";
        assertThrows(ClientServiceCreateException.class, () -> clientService.create(clientLogin, clientPassword));
    }

    // Read tests

    @Test
    public void clientManagerFindClientByIDTestPositive() throws ClientServiceReadException {
        Client foundClient = clientService.findByUUID(clientNo1.getClientID());
        assertNotNull(foundClient);
        assertEquals(clientNo1, foundClient);
    }

    @Test
    public void clientManagerFindClientByIDThatIsNotInTheDatabaseTestNegative() {
        Client client = new Client(UUID.randomUUID(), "SomeOtherLoginNo1", "SomeOtherPasswordNo1");
        assertNotNull(client);
        assertThrows(ClientServiceClientNotFoundException.class, () -> clientService.findByUUID(client.getClientID()));
    }

    @Test
    public void clientManagerFindClientByLoginTestPositive() throws ClientServiceReadException {
        Client foundClient = clientService.findByLogin(clientNo1.getClientLogin());
        assertNotNull(foundClient);
        assertEquals(clientNo1, foundClient);
    }

    @Test
    public void clientManagerFindClientByLoginThatIsNotInTheDatabaseTestNegative() {
        Client client = new Client(UUID.randomUUID(), "SomeOtherLoginNo1", "SomeOtherPasswordNo1");
        assertNotNull(client);
        assertThrows(ClientServiceClientNotFoundException.class, () -> clientService.findByLogin(client.getClientLogin()));
    }

    @Test
    public void clientManagerFindAllClientsMatchingLoginTestPositive() throws ClientServiceCreateException, ClientServiceReadException {
        clientService.create("NewClientLogin", "NewClientPassword");
        List<Client> listOfClients = clientService.findAllMatchingLogin("New");
        assertNotNull(listOfClients);
        assertFalse(listOfClients.isEmpty());
        assertEquals(1, listOfClients.size());
    }

    @Test
    public void clientManagerFindAllClientsTestPositive() throws ClientServiceReadException {
        List<Client> listOfClients = clientService.findAll();
        assertNotNull(listOfClients);
        assertFalse(listOfClients.isEmpty());
        assertEquals(2, listOfClients.size());
    }

    // Update tests

    @Test
    public void clientManagerUpdateClientTestPositive() throws ClientServiceUpdateException, ClientServiceReadException {
        String clientLoginBefore = clientNo1.getClientLogin();
        String clientPasswordBefore = clientNo1.getClientPassword();
        String newClientLogin = "OtherNewLoginNo1";
        String newClientPassword = "OtherNewPasswordNo1";
        clientNo1.setClientLogin(newClientLogin);
        clientNo1.setClientPassword(newClientPassword);
        clientService.update(clientNo1);
        Client foundClient = clientService.findByUUID(clientNo1.getClientID());
        String clientLoginAfter =  foundClient.getClientLogin();
        String clientPasswordAfter = foundClient.getClientPassword();
        assertNotNull(clientLoginAfter);
        assertNotNull(clientPasswordAfter);
        assertEquals(newClientLogin, clientLoginAfter);
        assertEquals(newClientPassword, clientPasswordAfter);
        assertNotEquals(clientLoginBefore, clientLoginAfter);
        assertNotEquals(clientPasswordBefore, clientPasswordAfter);
    }

    @Test
    public void clientManagerUpdateClientWithNullLoginTestNegative() {
        String clientLogin = null;
        String clientPassword = "SomeOtherPasswordNo2";
        clientNo1.setClientLogin(clientLogin);
        clientNo1.setClientPassword(clientPassword);
        assertThrows(ClientServiceUpdateException.class, () -> clientService.update(clientNo1));
    }

    @Test
    public void clientManagerUpdateClientWithEmptyLoginTestNegative() {
        String clientLogin = "";
        String clientPassword = "SomeOtherPasswordNo2";
        clientNo1.setClientLogin(clientLogin);
        clientNo1.setClientPassword(clientPassword);
        assertThrows(ClientServiceUpdateException.class, () -> clientService.update(clientNo1));
    }

    @Test
    public void clientManagerUpdateClientWithLoginTooShortTestNegative() {
        String clientLogin = "ddddfdd";
        String clientPassword = "SomeOtherPasswordNo2";
        clientNo1.setClientLogin(clientLogin);
        clientNo1.setClientPassword(clientPassword);
        assertThrows(ClientServiceUpdateException.class, () -> clientService.update(clientNo1));
    }

    @Test
    public void clientManagerUpdateClientWithLoginTooLongTestNegative() {
        String clientLogin = "ddddfddddfddddfddddfd";
        String clientPassword = "SomeOtherPasswordNo2";
        clientNo1.setClientLogin(clientLogin);
        clientNo1.setClientPassword(clientPassword);
        assertThrows(ClientServiceUpdateException.class, () -> clientService.update(clientNo1));
    }

    @Test
    public void clientManagerUpdateClientWithLoginLengthEqualTo8TestPositive() throws ClientServiceReadException {
        String clientLogin = "ddddfddd";
        String clientPassword = "SomeOtherPasswordNo2";
        clientNo1.setClientLogin(clientLogin);
        clientNo1.setClientPassword(clientPassword);
        assertDoesNotThrow(() -> clientService.update(clientNo1));
        Client foundClient = clientService.findByUUID(clientNo1.getClientID());
        assertEquals(clientLogin, foundClient.getClientLogin());
        assertEquals(clientPassword, foundClient.getClientPassword());
    }

    @Test
    public void clientManagerUpdateClientWithLoginLengthEqualTo20TestPositive() throws ClientServiceReadException {
        String clientLogin = "ddddfddddfddddfddddf";
        String clientPassword = "SomeOtherPasswordNo2";
        clientNo1.setClientLogin(clientLogin);
        clientNo1.setClientPassword(clientPassword);
        assertDoesNotThrow(() -> clientService.update(clientNo1));
        Client foundClient = clientService.findByUUID(clientNo1.getClientID());
        assertEquals(clientLogin, foundClient.getClientLogin());
        assertEquals(clientPassword, foundClient.getClientPassword());
    }

    @Test
    public void clientManagerUpdateClientWithLoginThatViolatesRegExTestNegative() {
        String clientLogin = "Some Invalid Login";
        String clientPassword = "SomeOtherPasswordNo2";
        clientNo1.setClientLogin(clientLogin);
        clientNo1.setClientPassword(clientPassword);
        assertThrows(ClientServiceUpdateException.class, () -> clientService.update(clientNo1));
    }

    @Test
    public void clientManagerUpdateClientWithNullPasswordTestNegative() {
        String clientLogin = "SomeOtherLoginNo2";
        String clientPassword = null;
        clientNo1.setClientLogin(clientLogin);
        clientNo1.setClientPassword(clientPassword);
        assertThrows(ClientServiceUpdateException.class, () -> clientService.update(clientNo1));
    }

    @Test
    public void clientManagerUpdateClientWithEmptyPasswordTestNegative() {
        String clientLogin = "SomeOtherLoginNo2";
        String clientPassword = "";
        clientNo1.setClientLogin(clientLogin);
        clientNo1.setClientPassword(clientPassword);
        assertThrows(ClientServiceUpdateException.class, () -> clientService.update(clientNo1));
    }

    @Test
    public void clientManagerUpdateClientWithPasswordTooShortTestNegative() {
        String clientLogin = "SomeOtherLoginNo2";
        String clientPassword = "ddddfdd";
        clientNo1.setClientLogin(clientLogin);
        clientNo1.setClientPassword(clientPassword);
        assertThrows(ClientServiceUpdateException.class, () -> clientService.update(clientNo1));
    }

    @Test
    public void clientManagerUpdateClientWithPasswordTooLongTestNegative() {
        String clientLogin = "SomeOtherLoginNo2";
        String clientPassword = "ddddfddddfddddfddddfddddfddddfddddfddddfd";
        clientNo1.setClientLogin(clientLogin);
        clientNo1.setClientPassword(clientPassword);
        assertThrows(ClientServiceUpdateException.class, () -> clientService.update(clientNo1));
    }

    @Test
    public void clientManagerUpdateClientWithPasswordLengthEqualTo8TestNegative() throws ClientServiceReadException {
        String clientLogin = "SomeOtherLoginNo2";
        String clientPassword = "ddddfddd";
        clientNo1.setClientLogin(clientLogin);
        clientNo1.setClientPassword(clientPassword);
        assertDoesNotThrow(() -> clientService.update(clientNo1));
        Client foundClient = clientService.findByUUID(clientNo1.getClientID());
        assertEquals(clientLogin, foundClient.getClientLogin());
        assertEquals(clientPassword, foundClient.getClientPassword());
    }

    @Test
    public void clientManagerUpdateClientWithPasswordLengthEqualTo40TestNegative() throws ClientServiceReadException {
        String clientLogin = "SomeOtherLoginNo2";
        String clientPassword = "ddddfddddfddddfddddfddddfddddfddddfddddf";
        clientNo1.setClientLogin(clientLogin);
        clientNo1.setClientPassword(clientPassword);
        assertDoesNotThrow(() -> clientService.update(clientNo1));
        Client foundClient = clientService.findByUUID(clientNo1.getClientID());
        assertEquals(clientLogin, foundClient.getClientLogin());
        assertEquals(clientPassword, foundClient.getClientPassword());
    }

    @Test
    public void clientManagerUpdateClientWithPasswordThatViolatesRegExTestNegative() {
        String clientLogin = "SomeOtherLoginNo2";
        String clientPassword = "Some Invalid Password";
        clientNo1.setClientLogin(clientLogin);
        clientNo1.setClientPassword(clientPassword);
        assertThrows(ClientServiceUpdateException.class, () -> clientService.update(clientNo1));
    }

    // Delete tests

    @Test
    public void clientManagerDeleteClientTestPositive() throws ClientServiceReadException, ClientServiceDeleteException {
        UUID removedClientUUID = clientNo1.getClientID();
        Client foundClient = clientService.findByUUID(removedClientUUID);
        assertNotNull(foundClient);
        assertEquals(clientNo1, foundClient);
        clientService.delete(removedClientUUID);
        assertThrows(ClientServiceReadException.class, () -> clientService.findByUUID(removedClientUUID));
    }

    @Test
    public void clientManagerDeleteClientThatIsNotInTheDatabaseTestNegative() {
        Client client = new Client(UUID.randomUUID(), "SomeOtherClientLoginNo3", "SomeOtherClientPasswordNo3");
        assertNotNull(client);
        assertThrows(ClientServiceDeleteException.class, () -> clientService.delete(client.getClientID()));
    }

    // Activate tests

    @Test
    public void clientManagerActivateClientTestPositive() throws ClientServiceReadException, ClientServiceDeactivationException, ClientServiceActivationException {
        clientService.deactivate(clientNo1.getClientID());

        Client foundClient = clientService.findByUUID(clientNo1.getClientID());
        assertNotNull(foundClient);
        assertFalse(foundClient.isClientStatusActive());
        clientService.activate(clientNo1.getClientID());
        foundClient = clientService.findByUUID(clientNo1.getClientID());
        assertNotNull(foundClient);
        assertTrue(foundClient.isClientStatusActive());
    }

    @Test
    public void clientManagerActivateClientThatIsNotInTheDatabaseTestNegative() {
        Client client = new Client(UUID.randomUUID(), "SomeOtherClientLoginNo3", "SomeOtherClientPasswordNo3");
        assertNotNull(client);
        assertThrows(ClientServiceActivationException.class, () -> clientService.activate(client.getClientID()));
    }

    // Deactivate tests

    @Test
    public void clientManagerDeactivateClientTestPositive() throws ClientServiceReadException, ClientServiceDeactivationException {
        Client foundClient = clientService.findByUUID(clientNo1.getClientID());
        assertNotNull(foundClient);
        assertEquals(clientNo1, foundClient);
        assertTrue(foundClient.isClientStatusActive());
        clientService.deactivate(clientNo1.getClientID());
        foundClient = clientService.findByUUID(clientNo1.getClientID());
        assertNotNull(foundClient);
        assertFalse(foundClient.isClientStatusActive());
    }

    @Test
    public void clientManagerDeactivateClientThatIsNotInTheDatabaseTestNegative() {
        Client client = new Client(UUID.randomUUID(), "SomeOtherClientLoginNo3", "SomeOtherClientPasswordNo3");
        assertNotNull(client);
        assertThrows(ClientServiceDeactivationException.class, () -> clientService.deactivate(client.getClientID()));
    }
}
