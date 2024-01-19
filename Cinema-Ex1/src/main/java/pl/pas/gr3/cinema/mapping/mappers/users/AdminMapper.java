package pl.pas.gr3.cinema.mapping.mappers.users;

import pl.pas.gr3.cinema.mapping.docs.users.AdminDoc;
import pl.pas.gr3.cinema.mapping.docs.users.ClientDoc;
import pl.pas.gr3.cinema.model.users.Admin;
import pl.pas.gr3.cinema.model.users.Client;

public class AdminMapper {
    public static AdminDoc toAdminDoc(Client admin) {
        AdminDoc adminDoc = new AdminDoc();
        adminDoc.setClientID(admin.getClientID());
        adminDoc.setClientLogin(admin.getClientLogin());
        adminDoc.setClientPassword(admin.getClientPassword());
        adminDoc.setClientStatusActive(admin.isClientStatusActive());
        return adminDoc;
    }

    public static Admin toAdmin(ClientDoc adminDoc) {
        return new Admin(adminDoc.getClientID(),
                adminDoc.getClientLogin(),
                adminDoc.getClientPassword(),
                adminDoc.isClientStatusActive());
    }
}
