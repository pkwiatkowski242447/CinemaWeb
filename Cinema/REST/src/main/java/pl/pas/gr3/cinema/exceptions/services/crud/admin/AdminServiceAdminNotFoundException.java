package pl.pas.gr3.cinema.exceptions.services.crud.admin;

public class AdminServiceAdminNotFoundException extends AdminServiceReadException {
    public AdminServiceAdminNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
