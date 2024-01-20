package pl.pas.gr3.mvc.controller.ticket;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.pas.gr3.dto.output.MovieDTO;
import pl.pas.gr3.dto.output.TicketDTO;
import pl.pas.gr3.dto.input.TicketInputDTO;
import pl.pas.gr3.dto.output.ClientDTO;
import pl.pas.gr3.mvc.dao.implementations.TicketDao;
import pl.pas.gr3.mvc.dao.interfaces.ITicketDao;
import pl.pas.gr3.mvc.dto.TicketWithUserAndMovie;
import pl.pas.gr3.mvc.exceptions.beans.tickets.TicketCreateException;
import pl.pas.gr3.mvc.exceptions.beans.tickets.TicketDeleteException;
import pl.pas.gr3.mvc.exceptions.beans.tickets.TicketReadException;
import pl.pas.gr3.mvc.exceptions.beans.tickets.TicketUpdateException;
import pl.pas.gr3.mvc.exceptions.daos.ticket.TicketDaoCreateException;
import pl.pas.gr3.mvc.exceptions.daos.ticket.TicketDaoDeleteException;
import pl.pas.gr3.mvc.exceptions.daos.ticket.TicketDaoReadException;
import pl.pas.gr3.mvc.exceptions.daos.ticket.TicketDaoUpdateException;

import java.io.Serializable;

@Getter @Setter
@NoArgsConstructor
@SessionScoped
@Named
public class TicketControllerBean implements Serializable {

    private TicketWithUserAndMovie createdTicket;
    private TicketWithUserAndMovie selectedTicket;

    private ClientDTO selectedClientDTO;
    private MovieDTO selectedMovieDTO;

    private String message;

    private ITicketDao ticketDao;

    @PostConstruct
    public void beanInit() {
        ticketDao = new TicketDao();
    }

    // Actions

    public void createTicket(TicketInputDTO ticketInputDTO) throws TicketCreateException {
        try {
            createdTicket = ticketDao.create(ticketInputDTO);
        } catch (TicketDaoCreateException exception) {
            throw new TicketCreateException(exception.getMessage());
        }
    }

    public void readTicketForChanges(TicketDTO ticketDTO) throws TicketReadException{
        try {
            selectedTicket = ticketDao.readTicketForChanges(ticketDTO);
        } catch (TicketDaoReadException exception) {
            throw new TicketReadException(exception.getMessage());
        }
    }

    public void updateTicket(TicketDTO ticketDTO) throws TicketUpdateException {
        try {
            ticketDao.updateTicket(ticketDTO);
        } catch (TicketDaoUpdateException exception) {
            throw new TicketUpdateException(exception.getMessage());
        }
    }

    public void deleteTicket(TicketDTO ticketDTO) throws TicketDeleteException {
        try {
            ticketDao.deleteTicket(ticketDTO);
        } catch (TicketDaoDeleteException exception) {
            throw new TicketDeleteException(exception.getMessage());
        }
    }
}
