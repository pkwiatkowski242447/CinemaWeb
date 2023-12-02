package pl.pas.gr3.cinema.managers.implementations;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import pl.pas.gr3.cinema.consts.repositories.MongoRepositoryConstants;
import pl.pas.gr3.cinema.exceptions.managers.crud.client.*;
import pl.pas.gr3.cinema.exceptions.repositories.ClientRepositoryException;
import pl.pas.gr3.cinema.managers.interfaces.UserManagerInterface;
import pl.pas.gr3.cinema.model.Ticket;
import pl.pas.gr3.cinema.model.users.Client;
import pl.pas.gr3.cinema.repositories.implementations.ClientRepository;

import java.io.Closeable;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ClientManager implements UserManagerInterface<Client>, Closeable {

    @Inject
    private ClientRepository clientRepository;

    public ClientManager() {
    }

    public ClientManager(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public Client create(String login, String password) throws ClientManagerCreateException {
        try {
            return this.clientRepository.createClient(login, password);
        } catch (ClientRepositoryException exception) {
            throw new ClientManagerCreateException(exception.getMessage(), exception);
        }
    }

    @Override
    public Client findByUUID(UUID clientID) throws ClientManagerReadException {
        try {
            return this.clientRepository.findClientByUUID(clientID);
        } catch (ClientRepositoryException exception) {
            throw new ClientManagerReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public Client findByLogin(String login) throws ClientManagerReadException {
        try {
            return this.clientRepository.findClientByLogin(login);
        } catch (ClientRepositoryException exception) {
            throw new ClientManagerReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public List<Client> findAllMatchingLogin(String loginToBeMatched) throws ClientManagerReadException {
        try {
            return this.clientRepository.findAllClientsMatchingLogin(loginToBeMatched);
        } catch (ClientRepositoryException exception) {
            throw new ClientManagerReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public List<Client> findAll() throws ClientManagerReadException {
        try {
            return this.clientRepository.findAllClients();
        } catch (ClientRepositoryException exception) {
            throw new ClientManagerReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public void update(Client client) throws ClientManagerUpdateException {
        try {
            this.clientRepository.updateClient(client);
        } catch (ClientRepositoryException exception) {
            throw new ClientManagerUpdateException(exception.getMessage(), exception);
        }
    }

    @Override
    public void activate(UUID clientID) throws ClientManagerActivationException {
        try {
            this.clientRepository.activate(this.clientRepository.findByUUID(clientID), MongoRepositoryConstants.CLIENT_SUBCLASS);
        } catch (ClientRepositoryException exception) {
            throw new ClientManagerActivationException(exception.getMessage(), exception);
        }
    }

    @Override
    public void deactivate(UUID clientID) throws ClientManagerDeactivationException {
        try {
            this.clientRepository.deactivate(this.clientRepository.findByUUID(clientID), MongoRepositoryConstants.CLIENT_SUBCLASS);
        } catch (ClientRepositoryException exception) {
            throw new ClientManagerDeactivationException(exception.getMessage(), exception);
        }
    }

    @Override
    public List<Ticket> getTicketsForClient(UUID clientID) throws ClientManagerReadException {
        try {
            return this.clientRepository.getListOfTicketsForClient(clientID, MongoRepositoryConstants.CLIENT_SUBCLASS);
        } catch (ClientRepositoryException exception) {
            throw new ClientManagerReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public void delete(UUID userID) throws ClientManagerDeleteException {
        try {
            this.clientRepository.delete(userID, MongoRepositoryConstants.CLIENT_SUBCLASS);
        } catch (ClientRepositoryException exception) {
            throw new ClientManagerDeleteException(exception.getMessage(), exception);
        }
    }

    @Override
    public void close() {
        this.clientRepository.close();
    }
}
