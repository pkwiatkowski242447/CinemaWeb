package pl.pas.gr3.cinema.controller.api;

import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface IUserService<Type> extends IService<Type> {

    // Read methods

    ResponseEntity<?> findByLogin(String login);
    ResponseEntity<?> findAllWithMatchingLogin(String loginToMatch);

    // Activate

    ResponseEntity<?> activate(UUID userID);

    // Deactivate

    ResponseEntity<?> deactivate(UUID userID);
}
