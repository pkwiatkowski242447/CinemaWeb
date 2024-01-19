package pl.pas.gr3.cinema.user_mappers;

import pl.pas.gr3.cinema.model.users.Admin;
import pl.pas.gr3.cinema.model.users.Staff;
import pl.pas.gr3.cinema.model.users.User;
import pl.pas.gr3.cinema.model.users.Client;

public class UserMapper {

    public static Client toClient(User user) {
        return new Client(user.getUserID(),
                user.getUserLogin(),
                user.getUserPassword(),
                user.isUserStatusActive());
    }

    public static Admin toAdmin(User user) {
        return new Admin(user.getUserID(),
                user.getUserLogin(),
                user.getUserPassword(),
                user.isUserStatusActive());
    }

    public static Staff toStaff(User user) {
        return new Staff(user.getUserID(),
                user.getUserLogin(),
                user.getUserPassword(),
                user.isUserStatusActive());
    }
}
