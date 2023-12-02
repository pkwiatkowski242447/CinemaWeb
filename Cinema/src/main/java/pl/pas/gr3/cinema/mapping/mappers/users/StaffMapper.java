package pl.pas.gr3.cinema.mapping.mappers.users;

import pl.pas.gr3.cinema.mapping.docs.users.ClientDoc;
import pl.pas.gr3.cinema.mapping.docs.users.StaffDoc;
import pl.pas.gr3.cinema.model.users.Client;
import pl.pas.gr3.cinema.model.users.Staff;

public class StaffMapper {

    public static StaffDoc toStaffDoc(Client staff) {
        StaffDoc staffDoc = new StaffDoc();
        staffDoc.setClientID(staff.getClientID());
        staffDoc.setClientLogin(staff.getClientLogin());
        staffDoc.setClientPassword(staff.getClientPassword());
        staffDoc.setClientStatusActive(staff.isClientStatusActive());
        return staffDoc;
    }

    public static Staff toStaff(ClientDoc staffDoc) {
        return new Staff(staffDoc.getClientID(),
                staffDoc.getClientLogin(),
                staffDoc.getClientPassword(),
                staffDoc.isClientStatusActive());
    }
}
