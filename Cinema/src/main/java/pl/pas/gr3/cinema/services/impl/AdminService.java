package pl.pas.gr3.cinema.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.pas.gr3.cinema.consts.model.UserConstants;
import pl.pas.gr3.cinema.exception.repositories.UserRepositoryException;
import pl.pas.gr3.cinema.exception.repositories.crud.user.UserRepositoryCreateUserDuplicateLoginException;
import pl.pas.gr3.cinema.exception.repositories.crud.user.UserRepositoryAdminNotFoundException;
import pl.pas.gr3.cinema.exception.services.crud.admin.*;
import pl.pas.gr3.cinema.services.api.UserServiceInterface;
import pl.pas.gr3.cinema.model.Ticket;
import pl.pas.gr3.cinema.model.users.Admin;
import pl.pas.gr3.cinema.repositories.impl.UserRepository;

import java.util.List;
import java.util.UUID;

@Service
public class AdminService implements UserServiceInterface<Admin> {

    private UserRepository userRepository;

    public AdminService() {
    }

    @Autowired
    public AdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Admin create(String login, String password) throws AdminServiceCreateException {
        try {
            return this.userRepository.createAdmin(login, password);
        } catch (UserRepositoryCreateUserDuplicateLoginException exception) {
            throw new AdminServiceCreateAdminDuplicateLoginException(exception.getMessage(), exception);
        } catch (UserRepositoryException exception) {
            throw new AdminServiceCreateException(exception.getMessage(), exception);
        }
    }

    @Override
    public Admin findByUUID(UUID adminID) throws AdminServiceReadException {
        try {
            return this.userRepository.findAdminByUUID(adminID);
        } catch (UserRepositoryAdminNotFoundException exception) {
            throw new AdminServiceAdminNotFoundException(exception.getMessage(), exception);
        } catch (UserRepositoryException exception) {
            throw new AdminServiceReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public Admin findByLogin(String login) throws AdminServiceReadException {
        try {
            return this.userRepository.findAdminByLogin(login);
        } catch (UserRepositoryAdminNotFoundException exception) {
            throw new AdminServiceAdminNotFoundException(exception.getMessage(), exception);
        } catch (UserRepositoryException exception) {
            throw new AdminServiceReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public List<Admin> findAllMatchingLogin(String loginToBeMatched) throws AdminServiceReadException {
        try {
            return this.userRepository.findAllAdminsMatchingLogin(loginToBeMatched);
        } catch (UserRepositoryException exception) {
            throw new AdminServiceReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public List<Admin> findAll() throws AdminServiceReadException {
        try {
            return this.userRepository.findAllAdmins();
        } catch (UserRepositoryException exception) {
            throw new AdminServiceReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public void update(Admin admin) throws AdminServiceUpdateException {
        try {
            this.userRepository.updateAdmin(admin);
        } catch (UserRepositoryException exception) {
            throw new AdminServiceUpdateException(exception.getMessage(), exception);
        }
    }

    @Override
    public void activate(UUID adminID) throws AdminServiceActivationException {
        try {
            this.userRepository.activate(this.userRepository.findAdminByUUID(adminID), UserConstants.ADMIN_DISCRIMINATOR);
        } catch (UserRepositoryException exception) {
            throw new AdminServiceActivationException(exception.getMessage(), exception);
        }
    }

    @Override
    public void deactivate(UUID adminID) throws AdminServiceDeactivationException {
        try {
            this.userRepository.deactivate(this.userRepository.findAdminByUUID(adminID), UserConstants.ADMIN_DISCRIMINATOR);
        } catch (UserRepositoryException exception) {
            throw new AdminServiceDeactivationException(exception.getMessage(), exception);
        }
    }

    @Override
    public List<Ticket> getTicketsForClient(UUID adminID) throws AdminServiceReadException {
        try {
            return this.userRepository.getListOfTicketsForClient(adminID, UserConstants.ADMIN_DISCRIMINATOR);
        } catch (UserRepositoryException exception) {
            throw new AdminServiceReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public void delete(UUID userID) throws AdminServiceDeleteException {
        try {
            this.userRepository.delete(userID, UserConstants.ADMIN_DISCRIMINATOR);
        } catch (UserRepositoryException exception) {
            throw new AdminServiceDeleteException(exception.getMessage(), exception);
        }
    }
}
