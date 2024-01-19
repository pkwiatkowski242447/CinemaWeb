package mapping.mappers.users;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.pas.gr3.cinema.mapping.docs.users.ClientDoc;
import pl.pas.gr3.cinema.mapping.mappers.users.ClientMapper;
import pl.pas.gr3.cinema.model.users.Client;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class ClientMapperTest {

    private static UUID uuidNo1;
    private static String loginNo1;
    private static String passwordNo1;
    private static boolean clientStatusActiveNo1;

    private ClientDoc clientDocNo1;
    private Client clientNo1;

    @BeforeAll
    public static void initializeVariables() {
        uuidNo1 = UUID.randomUUID();
        loginNo1 = "ExampleLoginNo1";
        passwordNo1 = "ExamplePasswordNo1";
        clientStatusActiveNo1 = true;
    }

    @BeforeEach
    public void initializeClientDocsAndClients() {
        clientDocNo1 = new ClientDoc(uuidNo1, loginNo1, passwordNo1, clientStatusActiveNo1);
        clientNo1 = new Client(uuidNo1, loginNo1, passwordNo1, clientStatusActiveNo1);
    }

    @SuppressWarnings("InstantiationOfUtilityClass")
    @Test
    public void clientMapperConstructorTest() {
        ClientMapper clientMapper = new ClientMapper();
        assertNotNull(clientMapper);
    }

    @Test
    public void clientMapperClientToClientDocTestPositive() {
        ClientDoc clientDoc = ClientMapper.toClientDoc(clientNo1);
        assertNotNull(clientDoc);
        assertEquals(clientNo1.getClientID(), clientDoc.getClientID());
        assertEquals(clientNo1.getClientLogin(), clientDoc.getClientLogin());
        assertEquals(clientNo1.getClientPassword(), clientDoc.getClientPassword());
        assertEquals(clientNo1.isClientStatusActive(), clientDoc.isClientStatusActive());
    }

    @Test
    public void clientMapperClientDocToClientTestPositive() {
        assertNotNull(clientDocNo1);
        Client client = ClientMapper.toClient(clientDocNo1);
        assertNotNull(client);
        assertEquals(clientDocNo1.getClientID(), client.getClientID());
        assertEquals(clientDocNo1.getClientLogin(), client.getClientLogin());
        assertEquals(clientDocNo1.getClientPassword(), client.getClientPassword());
        assertEquals(clientDocNo1.isClientStatusActive(), client.isClientStatusActive());
    }
}
