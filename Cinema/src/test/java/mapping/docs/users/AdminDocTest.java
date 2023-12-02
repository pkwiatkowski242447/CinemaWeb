package mapping.docs.users;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.pas.gr3.cinema.mapping.docs.users.AdminDoc;

import java.util.UUID;

import static org.bson.assertions.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class AdminDocTest {

    private static UUID uuidNo1;
    private static UUID uuidNo2;

    private static String loginNo1;
    private static String loginNo2;

    private static String passwordNo1;
    private static String passwordNo2;

    private static boolean clientStatusActiveNo1;
    private static boolean clientStatusActiveNo2;

    private AdminDoc adminDocNo1;
    private AdminDoc adminDocNo2;

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
    public void initializeAdminDocs() {
        adminDocNo1 = new AdminDoc(uuidNo1, loginNo1, passwordNo1, clientStatusActiveNo1);
        adminDocNo2 = new AdminDoc(uuidNo2, loginNo2, passwordNo2, clientStatusActiveNo2);
    }

    @Test
    public void adminDocNoArgsConstructorTest() {
        AdminDoc adminDoc = new AdminDoc();
        assertNotNull(adminDoc);
    }

    @Test
    public void adminDocAllArgsConstructorAndGettersTest() {
        AdminDoc adminDoc = new AdminDoc(uuidNo1, loginNo1, passwordNo1, clientStatusActiveNo1);
        assertNotNull(adminDoc);
        assertEquals(uuidNo1, adminDoc.getClientID());
        assertEquals(loginNo1, adminDoc.getClientLogin());
        assertEquals(passwordNo1, adminDoc.getClientPassword());
        assertEquals(clientStatusActiveNo1, adminDoc.isClientStatusActive());
    }

    @Test
    public void adminDocIdSetterTest() {
        UUID adminDocIdBefore = adminDocNo1.getClientID();
        assertNotNull(adminDocIdBefore);
        UUID newAdminDocId = UUID.randomUUID();
        assertNotNull(newAdminDocId);
        adminDocNo1.setClientID(newAdminDocId);
        UUID adminDocIdAfter = adminDocNo1.getClientID();
        assertNotNull(adminDocIdAfter);
        assertEquals(newAdminDocId, adminDocIdAfter);
        assertNotEquals(adminDocIdBefore, adminDocIdAfter);
    }

    @Test
    public void adminDocLoginSetterTest() {
        String adminDocLoginBefore = adminDocNo1.getClientLogin();
        assertNotNull(adminDocLoginBefore);
        String newAdminDocLogin = "NewLogin";
        assertNotNull(newAdminDocLogin);
        adminDocNo1.setClientLogin(newAdminDocLogin);
        String adminDocLoginAfter = adminDocNo1.getClientLogin();
        assertNotNull(adminDocLoginAfter);
        assertEquals(newAdminDocLogin, adminDocLoginAfter);
        assertNotEquals(adminDocLoginBefore, adminDocLoginAfter);
    }

    @Test
    public void adminDocPasswordSetterTest() {
        String adminDocPasswordBefore = adminDocNo2.getClientLogin();
        assertNotNull(adminDocPasswordBefore);
        String newAdminDocPassword = "NewPassword";
        assertNotNull(newAdminDocPassword);
        adminDocNo2.setClientLogin(newAdminDocPassword);
        String adminDocPasswordAfter = adminDocNo2.getClientLogin();
        assertNotNull(adminDocPasswordAfter);
        assertEquals(newAdminDocPassword, adminDocPasswordAfter);
        assertNotEquals(adminDocPasswordBefore, adminDocPasswordAfter);
    }

    @Test
    public void adminDocClientStatusActiveSetterTest() {
        boolean adminDocStatusActiveBefore = adminDocNo1.isClientStatusActive();
        boolean newAdminDocStatusActive = false;
        adminDocNo1.setClientStatusActive(newAdminDocStatusActive);
        boolean adminDocStatusActiveAfter = adminDocNo1.isClientStatusActive();
        assertEquals(newAdminDocStatusActive, adminDocStatusActiveAfter);
        assertNotEquals(adminDocStatusActiveBefore, adminDocStatusActiveAfter);
    }
}
