package mapping.docs.users;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.pas.gr3.cinema.mapping.docs.users.StaffDoc;

import java.util.UUID;

import static org.bson.assertions.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class StaffDocTest {

    private static UUID uuidNo1;
    private static UUID uuidNo2;

    private static String loginNo1;
    private static String loginNo2;

    private static String passwordNo1;
    private static String passwordNo2;

    private static boolean clientStatusActiveNo1;
    private static boolean clientStatusActiveNo2;

    private StaffDoc staffDocNo1;
    private StaffDoc staffDocNo2;

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
    public void initializeStaffDocs() {
        staffDocNo1 = new StaffDoc(uuidNo1, loginNo1, passwordNo1, clientStatusActiveNo1);
        staffDocNo2 = new StaffDoc(uuidNo2, loginNo2, passwordNo2, clientStatusActiveNo2);
    }

    @Test
    public void staffDocNoArgsConstructorTest() {
        StaffDoc staffDoc = new StaffDoc();
        assertNotNull(staffDoc);
    }

    @Test
    public void staffDocAllArgsConstructorAndGettersTest() {
        StaffDoc staffDoc = new StaffDoc(uuidNo1, loginNo1, passwordNo1, clientStatusActiveNo1);
        assertNotNull(staffDoc);
        assertEquals(uuidNo1, staffDoc.getClientID());
        assertEquals(loginNo1, staffDoc.getClientLogin());
        assertEquals(passwordNo1, staffDoc.getClientPassword());
        assertEquals(clientStatusActiveNo1, staffDoc.isClientStatusActive());
    }

    @Test
    public void staffDocIdSetterTest() {
        UUID staffDocIdBefore = staffDocNo1.getClientID();
        assertNotNull(staffDocIdBefore);
        UUID newStaffDocId = UUID.randomUUID();
        assertNotNull(newStaffDocId);
        staffDocNo1.setClientID(newStaffDocId);
        UUID staffDocIdAfter = staffDocNo1.getClientID();
        assertNotNull(staffDocIdAfter);
        assertEquals(newStaffDocId, staffDocIdAfter);
        assertNotEquals(staffDocIdBefore, staffDocIdAfter);
    }

    @Test
    public void staffDocLoginSetterTest() {
        String staffDocLoginBefore = staffDocNo1.getClientLogin();
        assertNotNull(staffDocLoginBefore);
        String newStaffDocLogin = "NewLogin";
        assertNotNull(newStaffDocLogin);
        staffDocNo1.setClientLogin(newStaffDocLogin);
        String staffDocLoginAfter = staffDocNo1.getClientLogin();
        assertNotNull(staffDocLoginAfter);
        assertEquals(newStaffDocLogin, staffDocLoginAfter);
        assertNotEquals(staffDocLoginBefore, staffDocLoginAfter);
    }

    @Test
    public void staffDocPasswordSetterTest() {
        String staffDocPasswordBefore = staffDocNo2.getClientLogin();
        assertNotNull(staffDocPasswordBefore);
        String newStaffDocPassword = "NewPassword";
        assertNotNull(newStaffDocPassword);
        staffDocNo2.setClientLogin(newStaffDocPassword);
        String staffDocPasswordAfter = staffDocNo2.getClientLogin();
        assertNotNull(staffDocPasswordAfter);
        assertEquals(newStaffDocPassword, staffDocPasswordAfter);
        assertNotEquals(staffDocPasswordBefore, staffDocPasswordAfter);
    }

    @Test
    public void staffDocClientStatusActiveSetterTest() {
        boolean staffDocStatusActiveBefore = staffDocNo1.isClientStatusActive();
        boolean newStaffDocStatusActive = false;
        staffDocNo1.setClientStatusActive(newStaffDocStatusActive);
        boolean staffDocStatusActiveAfter = staffDocNo1.isClientStatusActive();
        assertEquals(newStaffDocStatusActive, staffDocStatusActiveAfter);
        assertNotEquals(staffDocStatusActiveBefore, staffDocStatusActiveAfter);
    }
}
