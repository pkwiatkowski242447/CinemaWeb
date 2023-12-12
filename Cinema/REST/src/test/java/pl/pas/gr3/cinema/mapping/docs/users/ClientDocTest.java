package pl.pas.gr3.cinema.mapping.docs.users;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.bson.assertions.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ClientDocTest {

    private static UUID uuidNo1;
    private static UUID uuidNo2;

    private static String loginNo1;
    private static String loginNo2;

    private static String passwordNo1;
    private static String passwordNo2;

    private static boolean clientStatusActiveNo1;
    private static boolean clientStatusActiveNo2;

    private ClientDoc clientDocNo1;
    private ClientDoc clientDocNo2;

    @BeforeAll
    public static void initializeVariable() {
        uuidNo1 = UUID.randomUUID();
        uuidNo2 = UUID.randomUUID();
        loginNo1 = "ExampleLoginNo1";
        loginNo2 = "ExampleLoginNo2";
        passwordNo1 = "ExamplePasswordNo1";
        passwordNo2 = "ExamplePasswordNo2";
        clientStatusActiveNo1 = true;
        clientStatusActiveNo2 = false;
    }

    @BeforeEach
    public void initializeClientDocs() {
        clientDocNo1 = new ClientDoc(uuidNo1, loginNo1, passwordNo1, clientStatusActiveNo1);
        clientDocNo2 = new ClientDoc(uuidNo2, loginNo2, passwordNo2, clientStatusActiveNo2);
    }

    @Test
    public void clientDocNoArgsConstructorTest() {
        ClientDoc clientDoc = new ClientDoc();
        assertNotNull(clientDoc);
    }

    @Test
    public void clientDocAllArgsConstructorAndGettersTest() {
        ClientDoc clientDoc = new ClientDoc(uuidNo1, loginNo1, passwordNo1, clientStatusActiveNo1);
        assertNotNull(clientDoc);
        assertEquals(uuidNo1, clientDoc.getClientID());
        assertEquals(loginNo1, clientDoc.getClientLogin());
        assertEquals(passwordNo1, clientDoc.getClientPassword());
        assertEquals(clientStatusActiveNo1, clientDoc.isClientStatusActive());
    }

    @Test
    public void clientDocIdSetterTest() {
        UUID clientDocIdBefore = clientDocNo1.getClientID();
        assertNotNull(clientDocIdBefore);
        UUID newClientDocId = UUID.randomUUID();
        assertNotNull(newClientDocId);
        clientDocNo1.setClientID(newClientDocId);
        UUID clientDocIdAfter = clientDocNo1.getClientID();
        assertNotNull(clientDocIdAfter);
        assertEquals(newClientDocId, clientDocIdAfter);
        assertNotEquals(clientDocIdBefore, clientDocIdAfter);
    }

    @Test
    public void clientDocLoginSetterTest() {
        String clientDocLoginBefore = clientDocNo1.getClientLogin();
        assertNotNull(clientDocLoginBefore);
        String newClientDocLogin = "NewLogin";
        assertNotNull(newClientDocLogin);
        clientDocNo1.setClientLogin(newClientDocLogin);
        String clientDocLoginAfter = clientDocNo1.getClientLogin();
        assertNotNull(clientDocLoginAfter);
        assertEquals(newClientDocLogin, clientDocLoginAfter);
        assertNotEquals(clientDocLoginBefore, clientDocLoginAfter);
    }

    @Test
    public void clientDocPasswordSetterTest() {
        String clientDocPasswordBefore = clientDocNo2.getClientLogin();
        assertNotNull(clientDocPasswordBefore);
        String newClientDocPassword = "NewPassword";
        assertNotNull(newClientDocPassword);
        clientDocNo2.setClientLogin(newClientDocPassword);
        String clientDocPasswordAfter = clientDocNo2.getClientLogin();
        assertNotNull(clientDocPasswordAfter);
        assertEquals(newClientDocPassword, clientDocPasswordAfter);
        assertNotEquals(clientDocPasswordBefore, clientDocPasswordAfter);
    }

    @Test
    public void clientDocClientStatusActiveSetterTest() {
        boolean clientDocStatusActiveBefore = clientDocNo1.isClientStatusActive();
        boolean newClientDocStatusActive = false;
        clientDocNo1.setClientStatusActive(newClientDocStatusActive);
        boolean clientDocStatusActiveAfter = clientDocNo1.isClientStatusActive();
        assertEquals(newClientDocStatusActive, clientDocStatusActiveAfter);
        assertNotEquals(clientDocStatusActiveBefore, clientDocStatusActiveAfter);
    }
}
