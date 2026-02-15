package pl.pas.gr3.cinema.exception.services.crud.staff;

public class StaffServiceStaffNotFoundException extends StaffServiceReadException {
    public StaffServiceStaffNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
