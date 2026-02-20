package pl.pas.gr3.cinema.mapper;

import org.mapstruct.Mapper;
import pl.pas.gr3.cinema.dto.auth.AccountResponse;
import pl.pas.gr3.cinema.entity.account.Account;
import pl.pas.gr3.cinema.entity.account.Admin;
import pl.pas.gr3.cinema.entity.account.Staff;
import pl.pas.gr3.cinema.entity.account.Client;

@Mapper
public interface AccountMapper {

    default Client toClient(Account account) {
        Client client = new Client(account.getId(), account.getLogin(), account.getPassword());
        client.setActive(account.isActive());
        return client;
    }

    default Admin toAdmin(Account account) {
        Admin admin = new Admin(account.getId(), account.getLogin(), account.getPassword());
        admin.setActive(account.isActive());
        return admin;
    }

    default Staff toStaff(Account account) {
        Staff staff = new Staff(account.getId(), account.getLogin(), account.getPassword());
        staff.setActive(account.isActive());
        return staff;
    }

    AccountResponse toResponse(Client client);
    AccountResponse toResponse(Admin admin);
    AccountResponse toResponse(Staff staff);
}
