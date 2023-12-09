package pl.pas.gr3.cinema.managers.interfaces;

import pl.pas.gr3.cinema.exceptions.managers.GeneralManagerException;
import pl.pas.gr3.cinema.model.Ticket;

import java.util.List;
import java.util.UUID;

public interface UserManagerInterface<Type> extends ManagerInterface<Type> {

    // Create methods

    Type create(String login, String password) throws GeneralManagerException;

    // Read methods

    Type findByLogin(String login) throws GeneralManagerException;
    List<Type> findAllMatchingLogin(String loginToBeMatched) throws GeneralManagerException;

    // Update methods

    void update(Type element) throws GeneralManagerException;

    // Activate method

    void activate(UUID elementID) throws GeneralManagerException;

    // Deactivate method

    void deactivate(UUID elementID) throws GeneralManagerException;

    // Delete methods

    void delete(UUID userID) throws GeneralManagerException;

    // Other methods

    List<Ticket> getTicketsForClient(UUID userID) throws GeneralManagerException;

}
