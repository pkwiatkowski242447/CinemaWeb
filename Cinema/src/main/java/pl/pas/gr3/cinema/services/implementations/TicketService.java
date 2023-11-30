package pl.pas.gr3.cinema.services.implementations;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pl.pas.gr3.cinema.dto.TicketDTO;
import pl.pas.gr3.cinema.exceptions.managers.GeneralManagerException;
import pl.pas.gr3.cinema.managers.implementations.TicketManager;
import pl.pas.gr3.cinema.model.Ticket;
import pl.pas.gr3.cinema.services.interfaces.TicketServiceInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@ApplicationScoped
@Path("/tickets")
@Named
public class TicketService implements TicketServiceInterface {

    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Inject
    private TicketManager ticketManager;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response create(@QueryParam("time") String movieTime,
                           @QueryParam("client-id") UUID clientID,
                           @QueryParam("movie-id") UUID movieID,
                           @QueryParam("type") String ticketType) {
        try {
            Ticket ticket = this.ticketManager.create(movieTime, clientID, movieID, ticketType);
            Set<ConstraintViolation<Ticket>> violationSet = validator.validate(ticket);
            List<String> messages = violationSet.stream().map(ConstraintViolation::getMessage).toList();
            if (!violationSet.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).entity(messages).build();
            }
            TicketDTO ticketDTO = new TicketDTO(ticket.getTicketID(), ticket.getMovieTime(), ticket.getTicketFinalPrice(), ticket.getClient().getClientID(), ticket.getMovie().getMovieID());
            return Response.status(Response.Status.CREATED).entity(ticketDTO).build();
        } catch (GeneralManagerException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response findByUUID(@PathParam("id") UUID ticketID) {
        try {
            Ticket ticket = this.ticketManager.findByUUID(ticketID);
            TicketDTO ticketDTO = new TicketDTO(ticket.getTicketID(), ticket.getMovieTime(), ticket.getTicketFinalPrice(), ticket.getClient().getClientID(), ticket.getMovie().getMovieID());
            return Response.status(Response.Status.OK).entity(ticketDTO).build();
        } catch (GeneralManagerException exception) {
            return Response.status(Response.Status.NOT_FOUND).entity(exception.getMessage()).build();
        }
    }

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response findAll() {
        try {
            List<Ticket> listOfFoundTickets = this.ticketManager.findAll();
            List<TicketDTO> listOfDTOs = new ArrayList<>();
            for (Ticket ticket : listOfFoundTickets) {
                listOfDTOs.add(new TicketDTO(ticket.getTicketID(), ticket.getMovieTime(), ticket.getTicketFinalPrice(), ticket.getClient().getClientID(), ticket.getMovie().getMovieID()));
            }
            return Response.status(Response.Status.OK).entity(listOfDTOs).build();
        } catch (GeneralManagerException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response update(Ticket ticket) {
        try {
            Set<ConstraintViolation<Ticket>> violationSet = validator.validate(ticket);
            List<String> messages = violationSet.stream().map(ConstraintViolation::getMessage).toList();
            if (!violationSet.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).entity(messages).build();
            }
            this.ticketManager.update(ticket);
            return Response.status(Response.Status.OK).entity("Ticket object was modified.").build();
        } catch (GeneralManagerException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Override
    public Response delete(@PathParam("id") UUID ticketID) {
        try {
            this.ticketManager.delete(ticketID);
            return Response.status(Response.Status.OK).entity("Ticket with given ID was deleted.").build();
        } catch (GeneralManagerException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }
    }
}
