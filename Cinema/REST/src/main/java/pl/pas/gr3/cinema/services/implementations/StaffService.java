package pl.pas.gr3.cinema.services.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.pas.gr3.cinema.consts.repositories.MongoRepositoryConstants;
import pl.pas.gr3.cinema.exceptions.repositories.crud.client.ClientRepositoryStaffNotFoundException;
import pl.pas.gr3.cinema.exceptions.services.crud.staff.*;
import pl.pas.gr3.cinema.exceptions.repositories.ClientRepositoryException;
import pl.pas.gr3.cinema.exceptions.repositories.crud.client.ClientRepositoryCreateClientDuplicateLoginException;
import pl.pas.gr3.cinema.services.interfaces.UserManagerInterface;
import pl.pas.gr3.cinema.model.Ticket;
import pl.pas.gr3.cinema.model.users.Staff;
import pl.pas.gr3.cinema.repositories.implementations.ClientRepository;

import java.util.List;
import java.util.UUID;

@Service
public class StaffService implements UserManagerInterface<Staff> {

    private ClientRepository clientRepository;

    public StaffService() {
    }

    @Autowired
    public StaffService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public Staff create(String login, String password) throws StaffServiceCreateException {
        try {
            return this.clientRepository.createStaff(login, password);
        } catch (ClientRepositoryCreateClientDuplicateLoginException exception) {
            throw new StaffServiceCreateStaffDuplicateLoginException(exception.getMessage(), exception);
        } catch (ClientRepositoryException exception) {
            throw new StaffServiceCreateException(exception.getMessage(), exception);
        }
    }

    @Override
    public Staff findByUUID(UUID staffID) throws StaffServiceReadException {
        try {
            return this.clientRepository.findStaffByUUID(staffID);
        } catch (ClientRepositoryStaffNotFoundException exception) {
            throw new StaffServiceStaffNotFoundException(exception.getMessage(), exception);
        } catch (ClientRepositoryException exception) {
            throw new StaffServiceReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public Staff findByLogin(String login) throws StaffServiceReadException {
        try {
            return this.clientRepository.findStaffByLogin(login);
        } catch (ClientRepositoryStaffNotFoundException exception) {
            throw new StaffServiceStaffNotFoundException(exception.getMessage(), exception);
        } catch (ClientRepositoryException exception) {
            throw new StaffServiceReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public List<Staff> findAllMatchingLogin(String loginToBeMatched) throws StaffServiceReadException {
        try {
            return this.clientRepository.findAllStaffsMatchingLogin(loginToBeMatched);
        } catch (ClientRepositoryException exception) {
            throw new StaffServiceReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public List<Staff> findAll() throws StaffServiceReadException {
        try {
            return this.clientRepository.findAllStaffs();
        } catch (ClientRepositoryException exception) {
            throw new StaffServiceReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public void update(Staff staff) throws StaffServiceUpdateException {
        try {
            this.clientRepository.updateStaff(staff);
        } catch (ClientRepositoryException exception) {
            throw new StaffServiceUpdateException(exception.getMessage(), exception);
        }
    }

    @Override
    public void activate(UUID staffID) throws StaffServiceActivationException {
        try {
            this.clientRepository.activate(this.clientRepository.findByUUID(staffID), MongoRepositoryConstants.STAFF_SUBCLASS);
        } catch (ClientRepositoryException exception) {
            throw new StaffServiceActivationException(exception.getMessage(), exception);
        }
    }

    @Override
    public void deactivate(UUID staffID) throws StaffServiceDeactivationException {
        try {
            this.clientRepository.deactivate(this.clientRepository.findByUUID(staffID), MongoRepositoryConstants.STAFF_SUBCLASS);
        } catch (ClientRepositoryException exception) {
            throw new StaffServiceDeactivationException(exception.getMessage(), exception);
        }
    }

    @Override
    public List<Ticket> getTicketsForClient(UUID staffID) throws StaffServiceReadException {
        try {
            return this.clientRepository.getListOfTicketsForClient(staffID, MongoRepositoryConstants.STAFF_SUBCLASS);
        } catch (ClientRepositoryException exception) {
            throw new StaffServiceReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public void delete(UUID userID) throws StaffServiceDeleteException {
        try {
            this.clientRepository.delete(userID, MongoRepositoryConstants.STAFF_SUBCLASS);
        } catch (ClientRepositoryException exception) {
            throw new StaffServiceDeleteException(exception.getMessage(), exception);
        }
    }
}
