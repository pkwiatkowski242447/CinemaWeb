package mapping.mappers.users;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.pas.gr3.cinema.mapping.docs.users.AdminDoc;
import pl.pas.gr3.cinema.mapping.mappers.users.AdminMapper;
import pl.pas.gr3.cinema.model.users.Admin;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AdminMapperTest {

    private static UUID uuidNo1;
    private static String loginNo1;
    private static String passwordNo1;
    private static boolean clientStatusActiveNo1;

    private AdminDoc adminDocNo1;
    private Admin adminNo1;

    @BeforeAll
    public static void initializeVariables() {
        uuidNo1 = UUID.randomUUID();
        loginNo1 = "ExampleLoginNo1";
        passwordNo1 = "ExamplePasswordNo1";
        clientStatusActiveNo1 = true;
    }

    @BeforeEach
    public void initializeClientDocsAndClients() {
        adminDocNo1 = new AdminDoc(uuidNo1, loginNo1, passwordNo1, clientStatusActiveNo1);
        adminNo1 = new Admin(uuidNo1, loginNo1, passwordNo1, clientStatusActiveNo1);
    }

    @SuppressWarnings("InstantiationOfUtilityClass")
    @Test
    public void adminMapperConstructorTest() {
        AdminMapper adminMapper = new AdminMapper();
        assertNotNull(adminMapper);
    }

    @Test
    public void adminMapperAdminToAdminDocTestPositive() {
        AdminDoc adminDoc = AdminMapper.toAdminDoc(adminNo1);
        assertNotNull(adminDoc);
        assertEquals(adminNo1.getClientID(), adminDoc.getClientID());
        assertEquals(adminNo1.getClientLogin(), adminDoc.getClientLogin());
        assertEquals(adminNo1.getClientPassword(), adminDoc.getClientPassword());
        assertEquals(adminNo1.isClientStatusActive(), adminDoc.isClientStatusActive());
    }

    @Test
    public void adminMapperAdminDocToAdminTestPositive() {
        Admin admin = AdminMapper.toAdmin(adminDocNo1);
        assertNotNull(admin);
        assertEquals(adminDocNo1.getClientID(), admin.getClientID());
        assertEquals(adminDocNo1.getClientLogin(), admin.getClientLogin());
        assertEquals(adminDocNo1.getClientPassword(), admin.getClientPassword());
        assertEquals(adminDocNo1.isClientStatusActive(), admin.isClientStatusActive());
    }
}
