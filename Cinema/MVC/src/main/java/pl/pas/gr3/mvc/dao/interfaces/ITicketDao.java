package pl.pas.gr3.mvc.dao.interfaces;

import pl.pas.gr3.dto.output.TicketDTO;
import pl.pas.gr3.dto.input.TicketInputDTO;
import pl.pas.gr3.mvc.dto.TicketWithUserAndMovie;
import pl.pas.gr3.mvc.exceptions.daos.ticket.TicketDaoCreateException;
import pl.pas.gr3.mvc.exceptions.daos.ticket.TicketDaoDeleteException;
import pl.pas.gr3.mvc.exceptions.daos.ticket.TicketDaoReadException;
import pl.pas.gr3.mvc.exceptions.daos.ticket.TicketDaoUpdateException;

import java.util.List;

public interface ITicketDao {

    // Create methods

    TicketWithUserAndMovie create(TicketInputDTO ticketInputDTO) throws TicketDaoCreateException;

    // Read methods

    TicketWithUserAndMovie readTicketForChanges(TicketDTO ticketDTO) throws TicketDaoReadException;
    List<TicketWithUserAndMovie> findAll() throws TicketDaoReadException;

    // Update methods

    void updateTicket(TicketDTO ticketDTO) throws TicketDaoUpdateException;

    // Delete methods

    void deleteTicket(TicketDTO ticketDTO) throws TicketDaoDeleteException;
}
