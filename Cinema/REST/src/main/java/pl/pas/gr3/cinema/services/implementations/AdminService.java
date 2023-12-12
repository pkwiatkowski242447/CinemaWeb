package pl.pas.gr3.cinema.services.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.pas.gr3.cinema.consts.repositories.MongoRepositoryConstants;
import pl.pas.gr3.cinema.exceptions.repositories.crud.client.ClientRepositoryAdminNotFoundException;
import pl.pas.gr3.cinema.exceptions.services.crud.admin.*;
import pl.pas.gr3.cinema.exceptions.repositories.ClientRepositoryException;
import pl.pas.gr3.cinema.exceptions.repositories.crud.client.ClientRepositoryCreateClientDuplicateLoginException;
import pl.pas.gr3.cinema.services.interfaces.UserManagerInterface;
import pl.pas.gr3.cinema.model.Ticket;
import pl.pas.gr3.cinema.model.users.Admin;
import pl.pas.gr3.cinema.repositories.implementations.ClientRepository;

import java.io.Closeable;
import java.util.List;
import java.util.UUID;

@Service
public class AdminService implements UserManagerInterface<Admin>, Closeable {

    private ClientRepository clientRepository;

    public AdminService() {
    }

    @Autowired
    public AdminService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public Admin create(String login, String password) throws AdminServiceCreateException {
        try {
            return this.clientRepository.createAdmin(login, password);
        } catch (ClientRepositoryCreateClientDuplicateLoginException exception) {
            throw new AdminServiceCreateAdminDuplicateLoginException(exception.getMessage(), exception);
        } catch (ClientRepositoryException exception) {
            throw new AdminServiceCreateException(exception.getMessage(), exception);
        }
    }

    @Override
    public Admin findByUUID(UUID adminID) throws AdminServiceReadException {
        try {
            return this.clientRepository.findAdminByUUID(adminID);
        } catch (ClientRepositoryAdminNotFoundException exception) {
            throw new AdminServiceAdminNotFoundException(exception.getMessage(), exception);
        } catch (ClientRepositoryException exception) {
            throw new AdminServiceReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public Admin findByLogin(String login) throws AdminServiceReadException {
        try {
            return this.clientRepository.findAdminByLogin(login);
        } catch (ClientRepositoryAdminNotFoundException exception) {
            throw new AdminServiceAdminNotFoundException(exception.getMessage(), exception);
        } catch (ClientRepositoryException exception) {
            throw new AdminServiceReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public List<Admin> findAllMatchingLogin(String loginToBeMatched) throws AdminServiceReadException {
        try {
            return this.clientRepository.findAllAdminsMatchingLogin(loginToBeMatched);
        } catch (ClientRepositoryException exception) {
            throw new AdminServiceReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public List<Admin> findAll() throws AdminServiceReadException {
        try {
            return this.clientRepository.findAllAdmins();
        } catch (ClientRepositoryException exception) {
            throw new AdminServiceReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public void update(Admin admin) throws AdminServiceUpdateException {
        try {
            this.clientRepository.updateAdmin(admin);
        } catch (ClientRepositoryException exception) {
            throw new AdminServiceUpdateException(exception.getMessage(), exception);
        }
    }

    @Override
    public void activate(UUID adminID) throws AdminServiceActivationException {
        try {
            this.clientRepository.activate(this.clientRepository.findAdminByUUID(adminID), MongoRepositoryConstants.ADMIN_SUBCLASS);
        } catch (ClientRepositoryException exception) {
            throw new AdminServiceActivationException(exception.getMessage(), exception);
        }
    }

    @Override
    public void deactivate(UUID adminID) throws AdminServiceDeactivationException {
        try {
            this.clientRepository.deactivate(this.clientRepository.findAdminByUUID(adminID), MongoRepositoryConstants.ADMIN_SUBCLASS);
        } catch (ClientRepositoryException exception) {
            throw new AdminServiceDeactivationException(exception.getMessage(), exception);
        }
    }

    @Override
    public List<Ticket> getTicketsForClient(UUID adminID) throws AdminServiceReadException {
        try {
            return this.clientRepository.getListOfTicketsForClient(adminID, MongoRepositoryConstants.ADMIN_SUBCLASS);
        } catch (ClientRepositoryException exception) {
            throw new AdminServiceReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public void delete(UUID userID) throws AdminServiceDeleteException {
        try {
            this.clientRepository.delete(userID, MongoRepositoryConstants.ADMIN_SUBCLASS);
        } catch (ClientRepositoryException exception) {
            throw new AdminServiceDeleteException(exception.getMessage(), exception);
        }
    }

    @Override
    public void close() {
        this.clientRepository.close();
    }
}
