package pl.pas.gr3.cinema.dto.mappers.users;

import pl.pas.gr3.cinema.dto.json.users.AdminJson;
import pl.pas.gr3.cinema.dto.json.users.AdminJsonPassword;
import pl.pas.gr3.cinema.model.users.Admin;

public class AdminMapper {

    public static AdminJson toClientJsonFromClient(Admin admin) {
        AdminJson adminJson = new AdminJson();
        adminJson.setAdminID(admin.getClientID());
        adminJson.setAdminLogin(admin.getClientLogin());
        adminJson.setAdminStatusActive(admin.isClientStatusActive());
        return adminJson;
    }

    public static AdminJsonPassword toClientJsonWithPasswordFromClient(Admin admin) {
        AdminJsonPassword adminJsonPassword = new AdminJsonPassword();
        adminJsonPassword.setAdminID(admin.getClientID());
        adminJsonPassword.setAdminLogin(admin.getClientLogin());
        adminJsonPassword.setAdminPassword(admin.getClientPassword());
        adminJsonPassword.setAdminStatusActive(admin.isClientStatusActive());
        return adminJsonPassword;
    }

    public static Admin toClientFromClientJsonWithPassword(AdminJsonPassword adminJsonPassword) {
        return new Admin(adminJsonPassword.getAdminID(),
                adminJsonPassword.getAdminLogin(),
                adminJsonPassword.getAdminPassword(),
                adminJsonPassword.isAdminStatusActive());
    }
}
