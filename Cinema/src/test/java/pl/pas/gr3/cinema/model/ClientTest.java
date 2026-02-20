package pl.pas.gr3.cinema.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.pas.gr3.cinema.entity.account.Admin;
import pl.pas.gr3.cinema.entity.account.Client;
import pl.pas.gr3.cinema.entity.account.Staff;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ClientTest {

    private Client clientNo1;
    private Client clientNo2;
    private Client clientNo3;
    private Client clientNo4;
    private Client clientNo5;
    private Staff staffNo1;
    private Staff staffNo2;
    private Staff staffNo3;
    private Staff staffNo4;
    private Staff staffNo5;
    private Admin adminNo1;
    private Admin adminNo2;
    private Admin adminNo3;
    private Admin adminNo4;
    private Admin adminNo5;

    private static UUID uuidNo1;
    private static UUID uuidNo2;
    private static String loginNo1;
    private static String loginNo2;
    private static String passwordNo1;
    private static String passwordNo2;

    @BeforeAll
    static void initializeVariables() {
        uuidNo1 = UUID.randomUUID();
        uuidNo2 = UUID.randomUUID();
        loginNo1 = "loginNo1";
        loginNo2 = "loginNo2";
        passwordNo1 = "passwordNo1";
        passwordNo2 = "passwordNo2";
    }

    @BeforeEach
    void initializeClientObjects() {
        clientNo1 = new Client(uuidNo1, loginNo1, passwordNo1);
        clientNo2 = new Client(uuidNo2, loginNo2, passwordNo2);
        clientNo3 = new Client(clientNo1.getId(),
                clientNo1.getLogin(),
                clientNo1.getPassword());

        clientNo4 = new Client(uuidNo1, loginNo2, passwordNo2);
        clientNo5 = new Client(uuidNo1, loginNo1, passwordNo2);

        adminNo1 = new Admin(uuidNo1, loginNo1, passwordNo1);
        adminNo2 = new Admin(uuidNo2, loginNo2, passwordNo2);
        adminNo3 = new Admin(adminNo1.getId(),
                adminNo1.getLogin(),
                adminNo1.getPassword());

        adminNo4 = new Admin(uuidNo1, loginNo2, passwordNo2);
        adminNo5 = new Admin(uuidNo1, loginNo1, passwordNo2);

        staffNo1 = new Staff(uuidNo1, loginNo1, passwordNo1);
        staffNo2 = new Staff(uuidNo2, loginNo2, passwordNo2);
        staffNo3 = new Staff(staffNo1.getId(),
                staffNo1.getLogin(),
                staffNo1.getPassword());

        staffNo4 = new Staff(uuidNo1, loginNo2, passwordNo2);
        staffNo5 = new Staff(uuidNo1, loginNo1, passwordNo2);
    }

    @Test
    void clientConstructorAndGettersTest() {
        Client testClient = new Client(uuidNo1, loginNo1, passwordNo1);
        assertNotNull(testClient);
        assertEquals(uuidNo1, testClient.getId());
        assertEquals(loginNo1, testClient.getLogin());
        assertEquals(passwordNo1, testClient.getPassword());
    }

    @Test
    void staffConstructorAndGettersTest() {
        Staff testStaff = new Staff(uuidNo1, loginNo1, passwordNo1);
        assertNotNull(testStaff);
        assertEquals(uuidNo1, testStaff.getId());
        assertEquals(loginNo1, testStaff.getLogin());
        assertEquals(passwordNo1, testStaff.getPassword());
    }

    @Test
    void adminConstructorAndGettersTest() {
        Admin testAdmin = new Admin(uuidNo1, loginNo1, passwordNo1);
        assertNotNull(testAdmin);
        assertEquals(uuidNo1, testAdmin.getId());
        assertEquals(loginNo1, testAdmin.getLogin());
        assertEquals(passwordNo1, testAdmin.getPassword());
    }

    @Test
    void clientSetLoginTestPositive() {
        String loginBefore = clientNo1.getLogin();
        String newClientLogin = "newClientLogin";
        clientNo1.setLogin(newClientLogin);
        String loginAfter = clientNo1.getLogin();
        assertEquals(newClientLogin, loginAfter);
        assertNotEquals(loginBefore, loginAfter);
    }

    @Test
    void clientSetPasswordTestPositive() {
        String passwordBefore = clientNo1.getPassword();
        String newClientPassword = "newClientPassword";
        clientNo1.setPassword(newClientPassword);
        String passwordAfter = clientNo1.getPassword();
        assertEquals(newClientPassword, passwordAfter);
        assertNotEquals(passwordBefore, passwordAfter);
    }

    @Test
    void staffSetLoginTestPositive() {
        String loginBefore = staffNo1.getLogin();
        String newStaffLogin = "newStaffLogin";
        staffNo1.setLogin(newStaffLogin);
        String loginAfter = staffNo1.getLogin();
        assertEquals(newStaffLogin, loginAfter);
        assertNotEquals(loginBefore, loginAfter);
    }

    @Test
    void staffSetPasswordTestPositive() {
        String passwordBefore = staffNo1.getPassword();
        String newStaffPassword = "newStaffPassword";
        staffNo1.setPassword(newStaffPassword);
        String passwordAfter = staffNo1.getPassword();
        assertEquals(newStaffPassword, passwordAfter);
        assertNotEquals(passwordBefore, passwordAfter);
    }

    @Test
    void adminSetLoginTestPositive() {
        String loginBefore = adminNo1.getLogin();
        String newAdminLogin = "newAdminLogin";
        adminNo1.setLogin(newAdminLogin);
        String loginAfter = adminNo1.getLogin();
        assertEquals(newAdminLogin, loginAfter);
        assertNotEquals(loginBefore, loginAfter);
    }


    @Test
    void adminSetPasswordTestPositive() {
        String passwordBefore = adminNo1.getPassword();
        String newAdminPassword = "newAdminPassword";
        adminNo1.setPassword(newAdminPassword);
        String passwordAfter = adminNo1.getPassword();
        assertEquals(newAdminPassword, passwordAfter);
        assertNotEquals(passwordBefore, passwordAfter);
    }

    @Test
    void clientEqualsTestWithItself() {
        boolean equalsResult = clientNo1.equals(clientNo1);
        assertTrue(equalsResult);
    }

    @Test
    void clientEqualsTestWithNull() {
        boolean equalsResult = clientNo1.equals(null);
        assertFalse(equalsResult);
    }

    @Test
    void clientEqualsTestWithObjectOfDifferentClass() {
        boolean equalsResult = clientNo1.equals(new Object());
        assertFalse(equalsResult);
    }

    @Test
    void clientEqualsTestWithObjectOfTheSameClassButDifferent() {
        boolean equalsResult = clientNo1.equals(clientNo2);
        assertFalse(equalsResult);
    }

    @Test
    void clientEqualsTestWithObjectOfTheSameClassWithTheSameIDButWithDifferentLoginAndPassword() {
        boolean equalsResult = clientNo1.equals(clientNo4);
        assertFalse(equalsResult);
    }

    @Test
    void clientEqualsTestWithObjectOfTheSameClassWithTheSameIDAndLoginButWithDifferentPassword() {
        boolean equalsResult = clientNo1.equals(clientNo5);
        assertFalse(equalsResult);
    }

    @Test
    void clientEqualsTestWithObjectOfTheSameClassAndTheSame() {
        boolean equalsResult = clientNo1.equals(clientNo3);
        assertTrue(equalsResult);
    }

    @Test
    void clientHashCodeTestPositive() {
        int hashCodeFromClientNo1 = clientNo1.hashCode();
        int hashCodeFromClientNo3 = clientNo3.hashCode();
        assertEquals(hashCodeFromClientNo1, hashCodeFromClientNo3);
        assertEquals(clientNo1, clientNo3);
    }

    @Test
    void clientHashCodeTestNegative() {
        int hashCodeFromClientNo1 = clientNo1.hashCode();
        int hashCodeFromClientNo2 = clientNo2.hashCode();
        assertNotEquals(hashCodeFromClientNo1, hashCodeFromClientNo2);
    }

    @Test
    void adminEqualsTestWithItself() {
        boolean equalsResult = adminNo1.equals(adminNo1);
        assertTrue(equalsResult);
    }

    @Test
    void adminEqualsTestWithNull() {
        boolean equalsResult = adminNo1.equals(null);
        assertFalse(equalsResult);
    }

    @Test
    void adminEqualsTestWithObjectOfDifferentClass() {
        boolean equalsResult = adminNo1.equals(new Object());
        assertFalse(equalsResult);
    }

    @Test
    void adminEqualsTestWithObjectOfTheSameClassButDifferent() {
        boolean equalsResult = adminNo1.equals(adminNo2);
        assertFalse(equalsResult);
    }

    @Test
    void adminEqualsTestWithObjectOfTheSameClassWithTheSameIDButWithDifferentLoginAndPassword() {
        boolean equalsResult = adminNo1.equals(adminNo4);
        assertFalse(equalsResult);
    }

    @Test
    void adminEqualsTestWithObjectOfTheSameClassWithTheSameIDAndLoginButWithDifferentPassword() {
        boolean equalsResult = adminNo1.equals(adminNo5);
        assertFalse(equalsResult);
    }

    @Test
    void adminEqualsTestWithObjectOfTheSameClassAndTheSame() {
        boolean equalsResult = adminNo1.equals(adminNo3);
        assertTrue(equalsResult);
    }

    @Test
    void adminEqualsTestWithIdenticalClientObject() {
        boolean equalsResult = adminNo1.equals(clientNo1);
        assertFalse(equalsResult);
    }

    @Test
    void adminHashCodeTestPositive() {
        int hashCodeFromClientNo1 = adminNo1.hashCode();
        int hashCodeFromClientNo3 = adminNo3.hashCode();
        assertEquals(hashCodeFromClientNo1, hashCodeFromClientNo3);
        assertEquals(adminNo1, adminNo3);
    }

    @Test
    void adminHashCodeTestNegative() {
        int hashCodeFromClientNo1 = adminNo1.hashCode();
        int hashCodeFromClientNo2 = adminNo2.hashCode();
        assertNotEquals(hashCodeFromClientNo1, hashCodeFromClientNo2);
    }

    @Test
    void staffEqualsTestWithItself() {
        boolean equalsResult = staffNo1.equals(staffNo1);
        assertTrue(equalsResult);
    }

    @Test
    void staffEqualsTestWithNull() {
        boolean equalsResult = staffNo1.equals(null);
        assertFalse(equalsResult);
    }

    @Test
    void staffEqualsTestWithObjectOfDifferentClass() {
        boolean equalsResult = staffNo1.equals(new Object());
        assertFalse(equalsResult);
    }

    @Test
    void staffEqualsTestWithObjectOfTheSameClassButDifferent() {
        boolean equalsResult = staffNo1.equals(staffNo2);
        assertFalse(equalsResult);
    }

    @Test
    void staffEqualsTestWithObjectOfTheSameClassWithTheSameIDButWithDifferentLoginAndPassword() {
        boolean equalsResult = staffNo1.equals(staffNo4);
        assertFalse(equalsResult);
    }

    @Test
    void staffEqualsTestWithObjectOfTheSameClassWithTheSameIDAndLoginButWithDifferentPassword() {
        boolean equalsResult = staffNo1.equals(staffNo4);
        assertFalse(equalsResult);
    }

    @Test
    void staffEqualsTestWithObjectOfTheSameClassAndTheSame() {
        boolean equalsResult = staffNo1.equals(staffNo3);
        assertTrue(equalsResult);
    }

    @Test
    void staffEqualsTestWithIdenticalClientObject() {
        boolean equalsResult = staffNo1.equals(clientNo1);
        assertFalse(equalsResult);
    }

    @Test
    void staffHashCodeTestPositive() {
        int hashCodeFromClientNo1 = staffNo1.hashCode();
        int hashCodeFromClientNo3 = staffNo3.hashCode();
        assertEquals(hashCodeFromClientNo1, hashCodeFromClientNo3);
        assertEquals(staffNo1, staffNo3);
    }

    @Test
    void staffHashCodeTestNegative() {
        int hashCodeFromClientNo1 = staffNo1.hashCode();
        int hashCodeFromClientNo2 = staffNo2.hashCode();
        assertNotEquals(hashCodeFromClientNo1, hashCodeFromClientNo2);
    }

    @Test
    void clientToStringTest() {
        String clientResultToString = clientNo1.toString();
        assertNotNull(clientResultToString);
        assertFalse(clientResultToString.isEmpty());
    }

    @Test
    void staffToStringTest() {
        String staffResultToString = staffNo1.toString();
        assertNotNull(staffResultToString);
        assertFalse(staffResultToString.isEmpty());
    }

    @Test
    void adminToStringTest() {
        String adminResultToString = adminNo1.toString();
        assertNotNull(adminResultToString);
        assertFalse(adminResultToString.isEmpty());
    }
}