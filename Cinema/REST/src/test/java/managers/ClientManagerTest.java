package managers;

import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pas.gr3.cinema.exceptions.managers.crud.client.*;
import pl.pas.gr3.cinema.managers.implementations.ClientManager;
import pl.pas.gr3.cinema.model.users.Client;
import pl.pas.gr3.cinema.repositories.implementations.ClientRepository;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ClientManagerTest {

    private final static Logger logger = LoggerFactory.getLogger(ClientManagerTest.class);

    private final static String databaseName = "test";
    private static ClientRepository clientRepository;
    private static ClientManager clientManager;

    private Client clientNo1;
    private Client clientNo2;

    @BeforeAll
    public static void initialize() {
        clientRepository = new ClientRepository(databaseName);
        clientManager = new ClientManager(clientRepository);
    }

    @BeforeEach
    public void initializeSampleData() {
        this.clearTestData();
        try {
            clientNo1 = clientManager.create("UniqueClientLoginNo1", "UniqueClientPasswordNo1");
            clientNo2 = clientManager.create("UniqueClientLoginNo2", "UniqueClientPasswordNo2");
        } catch (ClientManagerCreateException exception) {
            logger.error(exception.getMessage());
        }
    }

    @AfterEach
    public void destroySampleData() {
        this.clearTestData();
    }

    private void clearTestData() {
        try {
            List<Client> listOfClients = clientManager.findAll();
            for (Client client : listOfClients) {
                clientManager.delete(client.getClientID());
            }
        } catch (ClientManagerReadException | ClientManagerDeleteException exception) {
            logger.error(exception.getMessage());
        }
    }

    @AfterAll
    public static void destroy() {
        clientManager.close();
    }

    // Create tests

    @Test
    public void clientManagerCreateClientTestPositive() throws ClientManagerCreateException {
        String clientLogin = "SomeOtherLoginNo1";
        String clientPassword = "SomeOtherPasswordNo1";
        Client client = clientManager.create(clientLogin, clientPassword);
        assertNotNull(client);
        assertEquals(clientLogin, client.getClientLogin());
        assertEquals(clientPassword, client.getClientPassword());
    }

    @Test
    public void clientManagerCreateClientWithNullLoginThatTestNegative() {
        String clientLogin = null;
        String clientPassword = "SomeOtherPasswordNo1";
        assertThrows(ClientManagerCreateException.class, () -> clientManager.create(clientLogin, clientPassword));
    }

    @Test
    public void clientManagerCreateClientWithEmptyLoginThatTestNegative() {
        String clientLogin = "";
        String clientPassword = "SomeOtherPasswordNo1";
        assertThrows(ClientManagerCreateException.class, () -> clientManager.create(clientLogin, clientPassword));
    }

    @Test
    public void clientManagerCreateClientWithLoginTooShortThatTestNegative() {
        String clientLogin = "ddddfdd";
        String clientPassword = "SomeOtherPasswordNo1";
        assertThrows(ClientManagerCreateException.class, () -> clientManager.create(clientLogin, clientPassword));
    }

    @Test
    public void clientManagerCreateClientWithLoginTooLongThatTestNegative() {
        String clientLogin = "ddddfddddfddddfddddfd";
        String clientPassword = "SomeOtherPasswordNo1";
        assertThrows(ClientManagerCreateException.class, () -> clientManager.create(clientLogin, clientPassword));
    }

    @Test
    public void clientManagerCreateClientWithLoginLengthEqualTo8ThatTestPositive() throws ClientManagerCreateException {
        String clientLogin = "ddddfddd";
        String clientPassword = "SomeOtherPasswordNo1";
        Client client = clientManager.create(clientLogin, clientPassword);
        assertNotNull(client);
        assertEquals(clientLogin, client.getClientLogin());
        assertEquals(clientPassword, client.getClientPassword());
    }

    @Test
    public void clientManagerCreateClientWithLoginLengthEqualTo20ThatTestPositive() throws ClientManagerCreateException {
        String clientLogin = "ddddfddddfddddfddddf";
        String clientPassword = "SomeOtherPasswordNo1";
        Client client = clientManager.create(clientLogin, clientPassword);
        assertNotNull(client);
        assertEquals(clientLogin, client.getClientLogin());
        assertEquals(clientPassword, client.getClientPassword());
    }

    @Test
    public void clientManagerCreateClientWithLoginThatDoesNotMeetRegExTestNegative() {
        String clientLogin = "Some Invalid Login";
        String clientPassword = "SomeOtherPasswordNo1";
        assertThrows(ClientManagerCreateException.class, () -> clientManager.create(clientLogin, clientPassword));
    }

    @Test
    public void clientManagerCreateClientWithLoginThatIsAlreadyInTheDatabaseTestNegative() {
        String clientLogin = clientNo1.getClientLogin();
        String clientPassword = "SomeOtherPasswordNo1";
        assertThrows(ClientManagerCreateException.class, () -> clientManager.create(clientLogin, clientPassword));
    }

    @Test
    public void clientManagerCreateClientWithNullPasswordThatTestNegative() {
        String clientLogin = "SomeOtherLoginNo1";
        String clientPassword = null;
        assertThrows(ClientManagerCreateException.class, () -> clientManager.create(clientLogin, clientPassword));
    }

    @Test
    public void clientManagerCreateClientWithEmptyPasswordThatTestNegative() {
        String clientLogin = "SomeOtherLoginNo1";
        String clientPassword = "";
        assertThrows(ClientManagerCreateException.class, () -> clientManager.create(clientLogin, clientPassword));
    }

    @Test
    public void clientManagerCreateClientWithPasswordTooShortThatTestNegative() {
        String clientLogin = "SomeOtherLoginNo1";
        String clientPassword = "ddddfdd";
        assertThrows(ClientManagerCreateException.class, () -> clientManager.create(clientLogin, clientPassword));
    }

    @Test
    public void clientManagerCreateClientWithPasswordTooLongThatTestNegative() {
        String clientLogin = "SomeOtherLoginNo1";
        String clientPassword = "ddddfddddfddddfddddfddddfddddfddddfddddfd";
        assertThrows(ClientManagerCreateException.class, () -> clientManager.create(clientLogin, clientPassword));
    }

    @Test
    public void clientManagerCreateClientWithPasswordLengthEqualTo8ThatTestPositive() throws ClientManagerCreateException {
        String clientLogin = "SomeOtherLoginNo1";
        String clientPassword = "ddddfddd";
        Client client = clientManager.create(clientLogin, clientPassword);
        assertNotNull(client);
        assertEquals(clientLogin, client.getClientLogin());
        assertEquals(clientPassword, client.getClientPassword());
    }

    @Test
    public void clientManagerCreateClientWithPasswordLengthEqualTo20ThatTestPositive() throws ClientManagerCreateException {
        String clientLogin = "SomeOtherLoginNo1";
        String clientPassword = "ddddfddddfddddfddddf";
        Client client = clientManager.create(clientLogin, clientPassword);
        assertNotNull(client);
        assertEquals(clientLogin, client.getClientLogin());
        assertEquals(clientPassword, client.getClientPassword());
    }

    @Test
    public void clientManagerCreateClientWithPasswordThatDoesNotMeetRegExTestNegative() {
        String clientLogin = "SomeOtherLoginNo1";
        String clientPassword = "Some Invalid Password";
        assertThrows(ClientManagerCreateException.class, () -> clientManager.create(clientLogin, clientPassword));
    }

    // Read tests

    @Test
    public void clientManagerFindClientByIDTestPositive() throws ClientManagerReadException {
        Client foundClient = clientManager.findByUUID(clientNo1.getClientID());
        assertNotNull(foundClient);
        assertEquals(clientNo1, foundClient);
    }

    @Test
    public void clientManagerFindClientByIDThatIsNotInTheDatabaseTestNegative() {
        Client client = new Client(UUID.randomUUID(), "SomeOtherLoginNo1", "SomeOtherPasswordNo1");
        assertNotNull(client);
        assertThrows(ClientManagerReadException.class, () -> clientManager.findByUUID(client.getClientID()));
    }

    @Test
    public void clientManagerFindClientByLoginTestPositive() throws ClientManagerReadException {
        Client foundClient = clientManager.findByLogin(clientNo1.getClientLogin());
        assertNotNull(foundClient);
        assertEquals(clientNo1, foundClient);
    }

    @Test
    public void clientManagerFindClientByLoginThatIsNotInTheDatabaseTestNegative() {
        Client client = new Client(UUID.randomUUID(), "SomeOtherLoginNo1", "SomeOtherPasswordNo1");
        assertNotNull(client);
        assertThrows(ClientManagerReadException.class, () -> clientManager.findByLogin(client.getClientLogin()));
    }

    @Test
    public void clientManagerFindAllClientsMatchingLoginTestPositive() throws ClientManagerCreateException, ClientManagerReadException {
        clientManager.create("NewClientLogin", "NewClientPassword");
        List<Client> listOfClients = clientManager.findAllMatchingLogin("New");
        assertNotNull(listOfClients);
        assertFalse(listOfClients.isEmpty());
        assertEquals(1, listOfClients.size());
    }

    @Test
    public void clientManagerFindAllClientsTestPositive() throws ClientManagerReadException {
        List<Client> listOfClients = clientManager.findAll();
        assertNotNull(listOfClients);
        assertFalse(listOfClients.isEmpty());
        assertEquals(2, listOfClients.size());
    }

    // Update tests

    @Test
    public void clientManagerUpdateClientTestPositive() throws ClientManagerUpdateException, ClientManagerReadException {
        String clientLoginBefore = clientNo1.getClientLogin();
        String clientPasswordBefore = clientNo1.getClientPassword();
        String newClientLogin = "OtherNewLoginNo1";
        String newClientPassword = "OtherNewPasswordNo1";
        clientNo1.setClientLogin(newClientLogin);
        clientNo1.setClientPassword(newClientPassword);
        clientManager.update(clientNo1);
        Client foundClient = clientManager.findByUUID(clientNo1.getClientID());
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
        assertThrows(ClientManagerUpdateException.class, () -> clientManager.update(clientNo1));
    }

    @Test
    public void clientManagerUpdateClientWithEmptyLoginTestNegative() {
        String clientLogin = "";
        String clientPassword = "SomeOtherPasswordNo2";
        clientNo1.setClientLogin(clientLogin);
        clientNo1.setClientPassword(clientPassword);
        assertThrows(ClientManagerUpdateException.class, () -> clientManager.update(clientNo1));
    }

    @Test
    public void clientManagerUpdateClientWithLoginTooShortTestNegative() {
        String clientLogin = "ddddfdd";
        String clientPassword = "SomeOtherPasswordNo2";
        clientNo1.setClientLogin(clientLogin);
        clientNo1.setClientPassword(clientPassword);
        assertThrows(ClientManagerUpdateException.class, () -> clientManager.update(clientNo1));
    }

    @Test
    public void clientManagerUpdateClientWithLoginTooLongTestNegative() {
        String clientLogin = "ddddfddddfddddfddddfd";
        String clientPassword = "SomeOtherPasswordNo2";
        clientNo1.setClientLogin(clientLogin);
        clientNo1.setClientPassword(clientPassword);
        assertThrows(ClientManagerUpdateException.class, () -> clientManager.update(clientNo1));
    }

    @Test
    public void clientManagerUpdateClientWithLoginLengthEqualTo8TestPositive() throws ClientManagerReadException {
        String clientLogin = "ddddfddd";
        String clientPassword = "SomeOtherPasswordNo2";
        clientNo1.setClientLogin(clientLogin);
        clientNo1.setClientPassword(clientPassword);
        assertDoesNotThrow(() -> clientManager.update(clientNo1));
        Client foundClient = clientManager.findByUUID(clientNo1.getClientID());
        assertEquals(clientLogin, foundClient.getClientLogin());
        assertEquals(clientPassword, foundClient.getClientPassword());
    }

    @Test
    public void clientManagerUpdateClientWithLoginLengthEqualTo20TestPositive() throws ClientManagerReadException {
        String clientLogin = "ddddfddddfddddfddddf";
        String clientPassword = "SomeOtherPasswordNo2";
        clientNo1.setClientLogin(clientLogin);
        clientNo1.setClientPassword(clientPassword);
        assertDoesNotThrow(() -> clientManager.update(clientNo1));
        Client foundClient = clientManager.findByUUID(clientNo1.getClientID());
        assertEquals(clientLogin, foundClient.getClientLogin());
        assertEquals(clientPassword, foundClient.getClientPassword());
    }

    @Test
    public void clientManagerUpdateClientWithLoginThatViolatesRegExTestNegative() {
        String clientLogin = "Some Invalid Login";
        String clientPassword = "SomeOtherPasswordNo2";
        clientNo1.setClientLogin(clientLogin);
        clientNo1.setClientPassword(clientPassword);
        assertThrows(ClientManagerUpdateException.class, () -> clientManager.update(clientNo1));
    }

    @Test
    public void clientManagerUpdateClientWithNullPasswordTestNegative() {
        String clientLogin = "SomeOtherLoginNo2";
        String clientPassword = null;
        clientNo1.setClientLogin(clientLogin);
        clientNo1.setClientPassword(clientPassword);
        assertThrows(ClientManagerUpdateException.class, () -> clientManager.update(clientNo1));
    }

    @Test
    public void clientManagerUpdateClientWithEmptyPasswordTestNegative() {
        String clientLogin = "SomeOtherLoginNo2";
        String clientPassword = "";
        clientNo1.setClientLogin(clientLogin);
        clientNo1.setClientPassword(clientPassword);
        assertThrows(ClientManagerUpdateException.class, () -> clientManager.update(clientNo1));
    }

    @Test
    public void clientManagerUpdateClientWithPasswordTooShortTestNegative() {
        String clientLogin = "SomeOtherLoginNo2";
        String clientPassword = "ddddfdd";
        clientNo1.setClientLogin(clientLogin);
        clientNo1.setClientPassword(clientPassword);
        assertThrows(ClientManagerUpdateException.class, () -> clientManager.update(clientNo1));
    }

    @Test
    public void clientManagerUpdateClientWithPasswordTooLongTestNegative() {
        String clientLogin = "SomeOtherLoginNo2";
        String clientPassword = "ddddfddddfddddfddddfddddfddddfddddfddddfd";
        clientNo1.setClientLogin(clientLogin);
        clientNo1.setClientPassword(clientPassword);
        assertThrows(ClientManagerUpdateException.class, () -> clientManager.update(clientNo1));
    }

    @Test
    public void clientManagerUpdateClientWithPasswordLengthEqualTo8TestNegative() throws ClientManagerReadException {
        String clientLogin = "SomeOtherLoginNo2";
        String clientPassword = "ddddfddd";
        clientNo1.setClientLogin(clientLogin);
        clientNo1.setClientPassword(clientPassword);
        assertDoesNotThrow(() -> clientManager.update(clientNo1));
        Client foundClient = clientManager.findByUUID(clientNo1.getClientID());
        assertEquals(clientLogin, foundClient.getClientLogin());
        assertEquals(clientPassword, foundClient.getClientPassword());
    }

    @Test
    public void clientManagerUpdateClientWithPasswordLengthEqualTo40TestNegative() throws ClientManagerReadException {
        String clientLogin = "SomeOtherLoginNo2";
        String clientPassword = "ddddfddddfddddfddddfddddfddddfddddfddddf";
        clientNo1.setClientLogin(clientLogin);
        clientNo1.setClientPassword(clientPassword);
        assertDoesNotThrow(() -> clientManager.update(clientNo1));
        Client foundClient = clientManager.findByUUID(clientNo1.getClientID());
        assertEquals(clientLogin, foundClient.getClientLogin());
        assertEquals(clientPassword, foundClient.getClientPassword());
    }

    @Test
    public void clientManagerUpdateClientWithPasswordThatViolatesRegExTestNegative() {
        String clientLogin = "SomeOtherLoginNo2";
        String clientPassword = "Some Invalid Password";
        clientNo1.setClientLogin(clientLogin);
        clientNo1.setClientPassword(clientPassword);
        assertThrows(ClientManagerUpdateException.class, () -> clientManager.update(clientNo1));
    }

    // Delete tests

    @Test
    public void clientManagerDeleteClientTestPositive() throws ClientManagerReadException, ClientManagerDeleteException {
        UUID removedClientUUID = clientNo1.getClientID();
        Client foundClient = clientManager.findByUUID(removedClientUUID);
        assertNotNull(foundClient);
        assertEquals(clientNo1, foundClient);
        clientManager.delete(removedClientUUID);
        assertThrows(ClientManagerReadException.class, () -> clientManager.findByUUID(removedClientUUID));
    }

    @Test
    public void clientManagerDeleteClientThatIsNotInTheDatabaseTestNegative() {
        Client client = new Client(UUID.randomUUID(), "SomeOtherClientLoginNo3", "SomeOtherClientPasswordNo3");
        assertNotNull(client);
        assertThrows(ClientManagerDeleteException.class, () -> clientManager.delete(client.getClientID()));
    }

    // Activate tests

    @Test
    public void clientManagerActivateClientTestPositive() throws ClientManagerReadException, ClientManagerDeactivationException, ClientManagerActivationException {
        clientManager.deactivate(clientNo1.getClientID());

        Client foundClient = clientManager.findByUUID(clientNo1.getClientID());
        assertNotNull(foundClient);
        assertFalse(foundClient.isClientStatusActive());
        clientManager.activate(clientNo1.getClientID());
        foundClient = clientManager.findByUUID(clientNo1.getClientID());
        assertNotNull(foundClient);
        assertTrue(foundClient.isClientStatusActive());
    }

    @Test
    public void clientManagerActivateClientThatIsNotInTheDatabaseTestNegative() {
        Client client = new Client(UUID.randomUUID(), "SomeOtherClientLoginNo3", "SomeOtherClientPasswordNo3");
        assertNotNull(client);
        assertThrows(ClientManagerActivationException.class, () -> clientManager.activate(client.getClientID()));
    }

    // Deactivate tests

    @Test
    public void clientManagerDeactivateClientTestPositive() throws ClientManagerReadException, ClientManagerDeactivationException {
        Client foundClient = clientManager.findByUUID(clientNo1.getClientID());
        assertNotNull(foundClient);
        assertEquals(clientNo1, foundClient);
        assertTrue(foundClient.isClientStatusActive());
        clientManager.deactivate(clientNo1.getClientID());
        foundClient = clientManager.findByUUID(clientNo1.getClientID());
        assertNotNull(foundClient);
        assertFalse(foundClient.isClientStatusActive());
    }

    @Test
    public void clientManagerDeactivateClientThatIsNotInTheDatabaseTestNegative() {
        Client client = new Client(UUID.randomUUID(), "SomeOtherClientLoginNo3", "SomeOtherClientPasswordNo3");
        assertNotNull(client);
        assertThrows(ClientManagerDeactivationException.class, () -> clientManager.deactivate(client.getClientID()));
    }
}
