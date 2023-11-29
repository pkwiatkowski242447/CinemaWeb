package pl.pas.gr3.cinema.managers.implementations;

import jakarta.inject.Inject;
import pl.pas.gr3.cinema.exceptions.managers.GeneralManagerException;
import pl.pas.gr3.cinema.exceptions.managers.crud.staff.*;
import pl.pas.gr3.cinema.exceptions.repositories.ClientRepositoryException;
import pl.pas.gr3.cinema.managers.interfaces.UserManagerInterface;
import pl.pas.gr3.cinema.model.Ticket;
import pl.pas.gr3.cinema.model.users.Staff;
import pl.pas.gr3.cinema.repositories.implementations.ClientRepository;

import java.io.Closeable;
import java.util.List;
import java.util.UUID;

public class StaffManager implements UserManagerInterface<Staff>, Closeable {

    @Inject
    private ClientRepository clientRepository;

    public StaffManager() {
    }

    public StaffManager(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public Staff create(String login, String password) throws StaffManagerCreateException {
        try {
            return this.clientRepository.createStaff(login, password);
        } catch (ClientRepositoryException exception) {
            throw new StaffManagerCreateException(exception.getMessage(), exception);
        }
    }

    @Override
    public Staff findByUUID(UUID staffID) throws StaffManagerReadException {
        try {
            return this.clientRepository.findStaffByUUID(staffID);
        } catch (ClientRepositoryException exception) {
            throw new StaffManagerReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public Staff findByLogin(String login) throws StaffManagerReadException {
        try {
            return this.clientRepository.findStaffByLogin(login);
        } catch (ClientRepositoryException exception) {
            throw new StaffManagerReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public List<Staff> findAllMatchingLogin(String loginToBeMatched) throws StaffManagerReadException {
        try {
            return this.clientRepository.findAllStaffsMatchingLogin(loginToBeMatched);
        } catch (ClientRepositoryException exception) {
            throw new StaffManagerReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public List<Staff> findAll() throws StaffManagerReadException {
        try {
            return this.clientRepository.findAllStaffs();
        } catch (ClientRepositoryException exception) {
            throw new StaffManagerReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public void update(Staff staff) throws StaffManagerUpdateException {
        try {
            this.clientRepository.updateStaff(staff);
        } catch (ClientRepositoryException exception) {
            throw new StaffManagerUpdateException(exception.getMessage(), exception);
        }
    }

    @Override
    public void activate(UUID staffID) throws StaffManagerActivationException {
        try {
            this.clientRepository.activate(this.clientRepository.findByUUID(staffID), "staff");
        } catch (ClientRepositoryException exception) {
            throw new StaffManagerActivationException(exception.getMessage(), exception);
        }
    }

    @Override
    public void deactivate(UUID staffID) throws StaffManagerDeactivationException {
        try {
            this.clientRepository.deactivate(this.clientRepository.findByUUID(staffID), "staff");
        } catch (ClientRepositoryException exception) {
            throw new StaffManagerDeactivationException(exception.getMessage(), exception);
        }
    }

    @Override
    public List<Ticket> getTicketsForClient(UUID staffID) throws StaffManagerReadException {
        try {
            return this.clientRepository.getListOfTicketsForClient(staffID, "staff");
        } catch (ClientRepositoryException exception) {
            throw new StaffManagerReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public void delete(UUID userID) throws StaffManagerDeleteException {
        try {
            this.clientRepository.delete(userID, "staff");
        } catch (ClientRepositoryException exception) {
            throw new StaffManagerDeleteException(exception.getMessage(), exception);
        }
    }

    @Override
    public void close() {
        this.clientRepository.close();
    }
}
