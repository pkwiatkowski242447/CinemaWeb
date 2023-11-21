package mapping.mappers.users;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.pas.gr3.cinema.mapping.docs.users.StaffDoc;
import pl.pas.gr3.cinema.mapping.mappers.users.StaffMapper;
import pl.pas.gr3.cinema.model.users.Staff;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class StaffMapperTest {

    private static UUID uuidNo1;
    private static String loginNo1;
    private static String passwordNo1;
    private static boolean clientStatusActiveNo1;

    private StaffDoc staffDocNo1;
    private Staff staffNo1;

    @BeforeAll
    public static void initializeVariables() {
        uuidNo1 = UUID.randomUUID();
        loginNo1 = "ExampleLoginNo1";
        passwordNo1 = "ExamplePasswordNo1";
        clientStatusActiveNo1 = true;
    }

    @BeforeEach
    public void initializeClientDocsAndClients() {
        staffDocNo1 = new StaffDoc(uuidNo1, loginNo1, passwordNo1, clientStatusActiveNo1);
        staffNo1 = new Staff(uuidNo1, loginNo1, passwordNo1, clientStatusActiveNo1);
    }

    @SuppressWarnings("InstantiationOfUtilityClass")
    @Test
    public void staffMapperConstructorTest() {
        StaffMapper staffMapper = new StaffMapper();
        assertNotNull(staffMapper);
    }

    @Test
    public void staffMapperStaffToStaffDocTestPositive() {
        StaffDoc staffDoc = StaffMapper.toStaffDoc(staffNo1);
        assertNotNull(staffDoc);
        assertEquals(staffNo1.getClientID(), staffDoc.getClientID());
        assertEquals(staffNo1.getClientLogin(), staffDoc.getClientLogin());
        assertEquals(staffNo1.getClientPassword(), staffDoc.getClientPassword());
        assertEquals(staffNo1.isClientStatusActive(), staffDoc.isClientStatusActive());
    }

    @Test
    public void staffMapperStaffDocToStaffTestPositive() {
        Staff staff = StaffMapper.toStaff(staffDocNo1);
        assertNotNull(staff);
        assertEquals(staffDocNo1.getClientID(), staff.getClientID());
        assertEquals(staffDocNo1.getClientLogin(), staff.getClientLogin());
        assertEquals(staffDocNo1.getClientPassword(), staff.getClientPassword());
        assertEquals(staffDocNo1.isClientStatusActive(), staff.isClientStatusActive());
    }
}
