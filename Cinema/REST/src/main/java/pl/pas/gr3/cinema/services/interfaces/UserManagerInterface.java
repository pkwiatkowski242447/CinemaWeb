package pl.pas.gr3.cinema.services.interfaces;

import pl.pas.gr3.cinema.exceptions.services.GeneralServiceException;
import pl.pas.gr3.cinema.model.Ticket;

import java.util.List;
import java.util.UUID;

public interface UserManagerInterface<Type> extends ManagerInterface<Type> {

    // Create methods

    Type create(String login, String password) throws GeneralServiceException;

    // Read methods

    Type findByLogin(String login) throws GeneralServiceException;
    List<Type> findAllMatchingLogin(String loginToBeMatched) throws GeneralServiceException;

    // Update methods

    void update(Type element) throws GeneralServiceException;

    // Activate method

    void activate(UUID elementID) throws GeneralServiceException;

    // Deactivate method

    void deactivate(UUID elementID) throws GeneralServiceException;

    // Delete methods

    void delete(UUID userID) throws GeneralServiceException;

    // Other methods

    List<Ticket> getTicketsForClient(UUID userID) throws GeneralServiceException;

}
