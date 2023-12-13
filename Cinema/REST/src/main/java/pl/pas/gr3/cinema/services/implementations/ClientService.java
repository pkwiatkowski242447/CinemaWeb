package pl.pas.gr3.cinema.services.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.pas.gr3.cinema.consts.repositories.MongoRepositoryConstants;
import pl.pas.gr3.cinema.exceptions.repositories.crud.client.ClientRepositoryClientNotFoundException;
import pl.pas.gr3.cinema.exceptions.services.crud.client.*;
import pl.pas.gr3.cinema.exceptions.repositories.ClientRepositoryException;
import pl.pas.gr3.cinema.exceptions.repositories.crud.client.ClientRepositoryCreateClientDuplicateLoginException;
import pl.pas.gr3.cinema.services.interfaces.UserManagerInterface;
import pl.pas.gr3.cinema.model.Ticket;
import pl.pas.gr3.cinema.model.users.Client;
import pl.pas.gr3.cinema.repositories.implementations.ClientRepository;

import java.util.List;
import java.util.UUID;

@Service
public class ClientService implements UserManagerInterface<Client> {

    private ClientRepository clientRepository;

    public ClientService() {
    }

    @Autowired
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }


    @Override
    public Client create(String login, String password) throws ClientServiceCreateException {
        try {
            return this.clientRepository.createClient(login, password);
        } catch (ClientRepositoryCreateClientDuplicateLoginException exception) {
            throw new ClientServiceCreateClientDuplicateLoginException(exception.getMessage(), exception);
        } catch (ClientRepositoryException exception) {
            throw new ClientServiceCreateException(exception.getMessage(), exception);
        }
    }

    @Override
    public Client findByUUID(UUID clientID) throws ClientServiceReadException {
        try {
            return this.clientRepository.findClientByUUID(clientID);
        } catch (ClientRepositoryClientNotFoundException exception) {
            throw new ClientServiceClientNotFoundException(exception.getMessage(), exception);
        } catch (ClientRepositoryException exception) {
            throw new ClientServiceReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public Client findByLogin(String login) throws ClientServiceReadException {
        try {
            return this.clientRepository.findClientByLogin(login);
        } catch (ClientRepositoryClientNotFoundException exception) {
            throw new ClientServiceClientNotFoundException(exception.getMessage(), exception);
        } catch (ClientRepositoryException exception) {
            throw new ClientServiceReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public List<Client> findAllMatchingLogin(String loginToBeMatched) throws ClientServiceReadException {
        try {
            return this.clientRepository.findAllClientsMatchingLogin(loginToBeMatched);
        } catch (ClientRepositoryException exception) {
            throw new ClientServiceReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public List<Client> findAll() throws ClientServiceReadException {
        try {
            return this.clientRepository.findAllClients();
        } catch (ClientRepositoryException exception) {
            throw new ClientServiceReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public void update(Client client) throws ClientServiceUpdateException {
        try {
            this.clientRepository.updateClient(client);
        } catch (ClientRepositoryException exception) {
            throw new ClientServiceUpdateException(exception.getMessage(), exception);
        }
    }

    @Override
    public void activate(UUID clientID) throws ClientServiceActivationException {
        try {
            this.clientRepository.activate(this.clientRepository.findByUUID(clientID), MongoRepositoryConstants.CLIENT_SUBCLASS);
        } catch (ClientRepositoryException exception) {
            throw new ClientServiceActivationException(exception.getMessage(), exception);
        }
    }

    @Override
    public void deactivate(UUID clientID) throws ClientServiceDeactivationException {
        try {
            this.clientRepository.deactivate(this.clientRepository.findByUUID(clientID), MongoRepositoryConstants.CLIENT_SUBCLASS);
        } catch (ClientRepositoryException exception) {
            throw new ClientServiceDeactivationException(exception.getMessage(), exception);
        }
    }

    @Override
    public List<Ticket> getTicketsForClient(UUID clientID) throws ClientServiceReadException {
        try {
            return this.clientRepository.getListOfTicketsForClient(clientID, MongoRepositoryConstants.CLIENT_SUBCLASS);
        } catch (ClientRepositoryException exception) {
            throw new ClientServiceReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public void delete(UUID userID) throws ClientServiceDeleteException {
        try {
            this.clientRepository.delete(userID, MongoRepositoryConstants.CLIENT_SUBCLASS);
        } catch (ClientRepositoryException exception) {
            throw new ClientServiceDeleteException(exception.getMessage(), exception);
        }
    }
}
