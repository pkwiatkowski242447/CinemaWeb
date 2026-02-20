package pl.pas.gr3.cinema.dto.messages;

public class UserMessages {

    public static final String NULL_IDENTIFIER = "Identifier of the user could not be null.";

    public static final String NULL_LOGIN = "No user login can be null.";
    public static final String LOGIN_TOO_SHORT = "No user login can be shorter than 8 characters.";
    public static final String LOGIN_TOO_LONG = "No user login can not be longer than 20 characters.";

    public static final String NULL_PASSWORD = "No user password can be null.";
    public static final String PASSWORD_TOO_SHORT = "No user password can be shorter than 8 characters.";
    public static final String PASSWORD_TOO_LONG = "No user password can be longer than 200 characters.";
}
