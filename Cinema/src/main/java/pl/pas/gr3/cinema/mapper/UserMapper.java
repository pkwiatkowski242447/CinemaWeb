package pl.pas.gr3.cinema.mapper;

import pl.pas.gr3.cinema.entity.account.Account;
import pl.pas.gr3.cinema.entity.account.Admin;
import pl.pas.gr3.cinema.entity.account.Staff;
import pl.pas.gr3.cinema.entity.account.Client;

public class UserMapper {

    public static Client toClient(Account account) {
        return new Client(account.getId(),
            account.getLogin(),
            account.getPassword(),
            account.isActive()
        );
    }

    public static Admin toAdmin(Account account) {
        return new Admin(account.getId(),
            account.getLogin(),
            account.getPassword(),
            account.isActive()
        );
    }

    public static Staff toStaff(Account account) {
        return new Staff(account.getId(),
            account.getLogin(),
            account.getPassword(),
            account.isActive()
        );
    }
}
