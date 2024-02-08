package pl.pas.gr3.mvc.controller.ticket;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.pas.gr3.dto.output.TicketDTO;
import pl.pas.gr3.mvc.dao.implementations.TicketDao;
import pl.pas.gr3.mvc.dao.interfaces.ITicketDao;
import pl.pas.gr3.mvc.dto.TicketWithUserAndMovie;
import pl.pas.gr3.mvc.exceptions.beans.tickets.TicketReadException;
import pl.pas.gr3.mvc.exceptions.daos.ticket.TicketDaoReadException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@ViewScoped
@Named
public class ReadAllTicketsBean implements Serializable {

    private List<TicketWithUserAndMovie> listOfExtTickets = new ArrayList<>();
    private String message;

    private ITicketDao ticketDao;

    @Inject
    private TicketControllerBean ticketControllerBean;

    @PostConstruct
    public void beanInit() {
        ticketDao = new TicketDao();
        this.findAllTickets();
    }

    public void findAllTickets() {
        try {
            List<TicketWithUserAndMovie> listOfTickets = ticketDao.findAll();
            if (listOfTickets.isEmpty()) {
                message = "Nie znaleziono żadnego biletu";
            }
            listOfExtTickets.clear();
            listOfExtTickets = listOfTickets;
        } catch (TicketDaoReadException exception) {
            message = exception.getMessage();
        }
    }

    public String updateTicket(TicketDTO ticketDTO) {
        try {
            ticketControllerBean.readTicketForChanges(ticketDTO);
            return "updateTicketAction";
        } catch (TicketReadException exception) {
            message = exception.getMessage();
            return null;
        }
    }

    public String deleteTicket(TicketDTO ticketDTO) {
        try {
            ticketControllerBean.readTicketForChanges(ticketDTO);
            return "deleteTicketAction";
        } catch (TicketReadException exception) {
            message = exception.getMessage();
            this.findAllTickets();
            return null;
        }
    }
}
