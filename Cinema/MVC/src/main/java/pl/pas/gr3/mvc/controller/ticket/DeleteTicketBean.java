package pl.pas.gr3.mvc.controller.ticket;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.pas.gr3.dto.TicketDTO;
import pl.pas.gr3.mvc.exceptions.beans.tickets.TicketDeleteException;

import java.io.Serializable;

@Getter @Setter
@NoArgsConstructor
@RequestScoped
@Named
public class DeleteTicketBean implements Serializable {

    private TicketDTO ticketDTO;

    @Inject
    private TicketControllerBean ticketControllerBean;

    @PostConstruct
    public void beanInit() {
        ticketDTO = ticketControllerBean.getSelectedTicket().getTicket();
    }

    public String deleteTicket() {
        try {
            ticketControllerBean.deleteTicket(ticketDTO);
            return "listOfAllTickets";
        } catch (TicketDeleteException exception) {
            ticketControllerBean.setMessage(exception.getMessage());
            return null;
        }
    }
}
