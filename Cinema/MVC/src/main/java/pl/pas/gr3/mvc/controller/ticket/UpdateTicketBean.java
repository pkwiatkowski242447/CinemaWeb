package pl.pas.gr3.mvc.controller.ticket;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.pas.gr3.dto.TicketDTO;
import pl.pas.gr3.mvc.exceptions.beans.tickets.TicketUpdateException;

import java.io.Serializable;

@Getter @Setter
@NoArgsConstructor
@RequestScoped
@Named
public class UpdateTicketBean implements Serializable {

    private TicketDTO ticketDTO;
    private String message;

    @Inject
    private TicketControllerBean ticketControllerBean;

    @PostConstruct
    public void beanInit() {
        ticketDTO = ticketControllerBean.getSelectedTicket().getTicket();
    }

    public String updateTicket() {
        try {
            ticketControllerBean.updateTicket(ticketDTO);
            return "listOfAllTickets";
        } catch (TicketUpdateException exception) {
            message = exception.getMessage();
            return null;
        }
    }
}
