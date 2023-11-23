package pl.pas.gr3.cinema.dto.mappers.users;

import pl.pas.gr3.cinema.dto.json.users.StaffJson;
import pl.pas.gr3.cinema.dto.json.users.StaffJsonPassword;
import pl.pas.gr3.cinema.model.users.Admin;
import pl.pas.gr3.cinema.model.users.Staff;

public class StaffMapper {

    public static StaffJson toClientJsonFromClient(Admin staff) {
        StaffJson staffJson = new StaffJson();
        staffJson.setStaffID(staff.getClientID());
        staffJson.setStaffLogin(staff.getClientLogin());
        staffJson.setStaffStatusActive(staff.isClientStatusActive());
        return staffJson;
    }

    public static StaffJsonPassword toClientJsonWithPasswordFromClient(Admin staff) {
        StaffJsonPassword staffJsonPassword = new StaffJsonPassword();
        staffJsonPassword.setStaffID(staff.getClientID());
        staffJsonPassword.setStaffLogin(staff.getClientLogin());
        staffJsonPassword.setStaffPassword(staff.getClientPassword());
        staffJsonPassword.setStaffStatusActive(staff.isClientStatusActive());
        return staffJsonPassword;
    }

    public static Staff toClientFromClientJsonWithPassword(StaffJsonPassword staffJsonPassword) {
        return new Staff(staffJsonPassword.getStaffID(),
                staffJsonPassword.getStaffLogin(),
                staffJsonPassword.getStaffPassword(),
                staffJsonPassword.isStaffStatusActive());
    }
}
