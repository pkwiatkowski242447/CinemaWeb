package pl.pas.gr3.cinema.controllers.interfaces;

import jakarta.ws.rs.core.Response;

import java.util.UUID;

public interface UserServiceInterface<Type> extends ServiceInterface<Type> {

    // Read methods

    Response findByLogin(String login);
    Response findAllWithMatchingLogin(String loginToMatch);

    // Activate

    Response activate(UUID userID);

    // Deactivate

    Response deactivate(UUID userID);

    // Other methods

    Response getTicketsForCertainUser(UUID userID);
}
