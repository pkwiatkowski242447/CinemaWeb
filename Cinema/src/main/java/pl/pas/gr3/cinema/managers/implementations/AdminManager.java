package pl.pas.gr3.cinema.managers.implementations;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import pl.pas.gr3.cinema.exceptions.managers.crud.admin.*;
import pl.pas.gr3.cinema.exceptions.repositories.ClientRepositoryException;
import pl.pas.gr3.cinema.managers.interfaces.UserManagerInterface;
import pl.pas.gr3.cinema.model.Ticket;
import pl.pas.gr3.cinema.model.users.Admin;
import pl.pas.gr3.cinema.repositories.implementations.ClientRepository;

import java.io.Closeable;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class AdminManager implements UserManagerInterface<Admin>, Closeable {

    @Inject
    private ClientRepository clientRepository;

    public AdminManager() {
    }

    public AdminManager(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public Admin create(String login, String password) throws AdminManagerCreateException {
        try {
            return this.clientRepository.createAdmin(login, password);
        } catch (ClientRepositoryException exception) {
            throw new AdminManagerCreateException(exception.getMessage(), exception);
        }
    }

    @Override
    public Admin findByUUID(UUID adminID) throws AdminManagerReadException {
        try {
            return this.clientRepository.findAdminByUUID(adminID);
        } catch (ClientRepositoryException exception) {
            throw new AdminManagerReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public Admin findByLogin(String login) throws AdminManagerReadException {
        try {
            return this.clientRepository.findAdminByLogin(login);
        } catch (ClientRepositoryException exception) {
            throw new AdminManagerReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public List<Admin> findAllMatchingLogin(String loginToBeMatched) throws AdminManagerReadException {
        try {
            return this.clientRepository.findAllAdminsMatchingLogin(loginToBeMatched);
        } catch (ClientRepositoryException exception) {
            throw new AdminManagerReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public List<Admin> findAll() throws AdminManagerReadException {
        try {
            return this.clientRepository.findAllAdmins();
        } catch (ClientRepositoryException exception) {
            throw new AdminManagerReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public void update(Admin admin) throws AdminManagerUpdateException {
        try {
            this.clientRepository.updateAdmin(admin);
        } catch (ClientRepositoryException exception) {
            throw new AdminManagerUpdateException(exception.getMessage(), exception);
        }
    }

    @Override
    public void activate(UUID adminID) throws AdminManagerActivationException {
        try {
            this.clientRepository.activate(this.clientRepository.findAdminByUUID(adminID), "admin");
        } catch (ClientRepositoryException exception) {
            throw new AdminManagerActivationException(exception.getMessage(), exception);
        }
    }

    @Override
    public void deactivate(UUID adminID) throws AdminManagerDeactivationException {
        try {
            this.clientRepository.deactivate(this.clientRepository.findAdminByUUID(adminID), "admin");
        } catch (ClientRepositoryException exception) {
            throw new AdminManagerDeactivationException(exception.getMessage(), exception);
        }
    }

    @Override
    public List<Ticket> getTicketsForClient(UUID adminID) throws AdminManagerReadException {
        try {
            return this.clientRepository.getListOfTicketsForClient(adminID, "admin");
        } catch (ClientRepositoryException exception) {
            throw new AdminManagerReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public void delete(UUID userID) throws AdminManagerDeleteException {
        try {
            this.clientRepository.delete(userID, "admin");
        } catch (ClientRepositoryException exception) {
            throw new AdminManagerDeleteException(exception.getMessage(), exception);
        }
    }

    @Override
    public void close() {
        this.clientRepository.close();
    }
}
