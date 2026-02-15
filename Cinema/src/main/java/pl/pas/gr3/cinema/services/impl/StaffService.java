package pl.pas.gr3.cinema.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.pas.gr3.cinema.consts.model.UserConstants;
import pl.pas.gr3.cinema.exception.repositories.UserRepositoryException;
import pl.pas.gr3.cinema.exception.repositories.crud.user.UserRepositoryCreateUserDuplicateLoginException;
import pl.pas.gr3.cinema.exception.repositories.crud.user.UserRepositoryStaffNotFoundException;
import pl.pas.gr3.cinema.exception.services.crud.staff.*;
import pl.pas.gr3.cinema.repositories.impl.UserRepository;
import pl.pas.gr3.cinema.services.api.UserServiceInterface;
import pl.pas.gr3.cinema.model.Ticket;
import pl.pas.gr3.cinema.model.users.Staff;

import java.util.List;
import java.util.UUID;

@Service
public class StaffService implements UserServiceInterface<Staff> {

    private UserRepository userRepository;

    public StaffService() {
    }

    @Autowired
    public StaffService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Staff create(String login, String password) throws StaffServiceCreateException {
        try {
            return this.userRepository.createStaff(login, password);
        } catch (UserRepositoryCreateUserDuplicateLoginException exception) {
            throw new StaffServiceCreateStaffDuplicateLoginException(exception.getMessage(), exception);
        } catch (UserRepositoryException exception) {
            throw new StaffServiceCreateException(exception.getMessage(), exception);
        }
    }

    @Override
    public Staff findByUUID(UUID staffID) throws StaffServiceReadException {
        try {
            return this.userRepository.findStaffByUUID(staffID);
        } catch (UserRepositoryStaffNotFoundException exception) {
            throw new StaffServiceStaffNotFoundException(exception.getMessage(), exception);
        } catch (UserRepositoryException exception) {
            throw new StaffServiceReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public Staff findByLogin(String login) throws StaffServiceReadException {
        try {
            return this.userRepository.findStaffByLogin(login);
        } catch (UserRepositoryStaffNotFoundException exception) {
            throw new StaffServiceStaffNotFoundException(exception.getMessage(), exception);
        } catch (UserRepositoryException exception) {
            throw new StaffServiceReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public List<Staff> findAllMatchingLogin(String loginToBeMatched) throws StaffServiceReadException {
        try {
            return this.userRepository.findAllStaffsMatchingLogin(loginToBeMatched);
        } catch (UserRepositoryException exception) {
            throw new StaffServiceReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public List<Staff> findAll() throws StaffServiceReadException {
        try {
            return this.userRepository.findAllStaffs();
        } catch (UserRepositoryException exception) {
            throw new StaffServiceReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public void update(Staff staff) throws StaffServiceUpdateException {
        try {
            this.userRepository.updateStaff(staff);
        } catch (UserRepositoryException exception) {
            throw new StaffServiceUpdateException(exception.getMessage(), exception);
        }
    }

    @Override
    public void activate(UUID staffID) throws StaffServiceActivationException {
        try {
            this.userRepository.activate(this.userRepository.findByUUID(staffID), UserConstants.STAFF_DISCRIMINATOR);
        } catch (UserRepositoryException exception) {
            throw new StaffServiceActivationException(exception.getMessage(), exception);
        }
    }

    @Override
    public void deactivate(UUID staffID) throws StaffServiceDeactivationException {
        try {
            this.userRepository.deactivate(this.userRepository.findByUUID(staffID), UserConstants.STAFF_DISCRIMINATOR);
        } catch (UserRepositoryException exception) {
            throw new StaffServiceDeactivationException(exception.getMessage(), exception);
        }
    }

    @Override
    public List<Ticket> getTicketsForClient(UUID staffID) throws StaffServiceReadException {
        try {
            return this.userRepository.getListOfTicketsForClient(staffID, UserConstants.STAFF_DISCRIMINATOR);
        } catch (UserRepositoryException exception) {
            throw new StaffServiceReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public void delete(UUID userID) throws StaffServiceDeleteException {
        try {
            this.userRepository.delete(userID, UserConstants.STAFF_DISCRIMINATOR);
        } catch (UserRepositoryException exception) {
            throw new StaffServiceDeleteException(exception.getMessage(), exception);
        }
    }
}
