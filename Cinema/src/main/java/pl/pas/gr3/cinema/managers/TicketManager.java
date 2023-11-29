package pl.pas.gr3.cinema.managers;

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
import pl.pas.gr3.cinema.exceptions.repositories.TicketRepositoryException;
import pl.pas.gr3.cinema.model.Ticket;
import pl.pas.gr3.cinema.model.TicketType;
import pl.pas.gr3.cinema.repositories.implementations.TicketRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@ApplicationScoped
@Path("/tickets")
@Named
public class TicketManager extends Manager<Ticket> {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Inject
    private TicketRepository ticketRepository;

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@QueryParam("time") String movieTime,
                           @QueryParam("client-id") UUID clientID,
                           @QueryParam("movie-id") UUID movieID,
                           @QueryParam("type") String ticketType) {
        LocalDateTime realMovieTime = LocalDateTime.parse(movieTime);
        TicketType typeOfTicket;
        if (ticketType.equals("reduced")) {
            typeOfTicket = TicketType.REDUCED;
        } else if (ticketType.equals("normal")) {
            typeOfTicket = TicketType.NORMAL;
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid ticket type.").build();
        }
        try {
            Ticket ticket = this.ticketRepository.create(realMovieTime, clientID, movieID, typeOfTicket);
            Set<ConstraintViolation<Ticket>> violationSet = validator.validate(ticket);
            List<String> messages = violationSet.stream().map(ConstraintViolation::getMessage).toList();
            if (!violationSet.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).entity(messages).build();
            }
            TicketDTO ticketDTO = new TicketDTO(ticket.getTicketID(), ticket.getMovieTime(), ticket.getTicketFinalPrice(), ticket.getClient().getClientID(), ticket.getMovie().getMovieID());
            return Response.status(Response.Status.CREATED).entity(ticketDTO).build();
        } catch (TicketRepositoryException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response findByUUID(@PathParam("id") UUID ticketID) {
        try {
            Ticket ticket = this.ticketRepository.findByUUID(ticketID);
            TicketDTO ticketDTO = new TicketDTO(ticket.getTicketID(), ticket.getMovieTime(), ticket.getTicketFinalPrice(), ticket.getClient().getClientID(), ticket.getMovie().getMovieID());
            return Response.status(Response.Status.OK).entity(ticketDTO).build();
        } catch (TicketRepositoryException exception) {
            return Response.status(Response.Status.NOT_FOUND).entity(exception.getMessage()).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response findAll() {
        try {
            List<Ticket> listOfFoundTickets = this.ticketRepository.findAll();
            List<TicketDTO> listOfDTOs = new ArrayList<>();
            for (Ticket ticket : listOfFoundTickets) {
                listOfDTOs.add(new TicketDTO(ticket.getTicketID(), ticket.getMovieTime(), ticket.getTicketFinalPrice(), ticket.getClient().getClientID(), ticket.getMovie().getMovieID()));
            }
            return Response.status(Response.Status.OK).entity(listOfDTOs).build();
        } catch (TicketRepositoryException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response update(Ticket ticket) {
        try {
            this.ticketRepository.update(ticket);
            return Response.status(Response.Status.OK).entity("Ticket object was modified.").build();
        } catch (TicketRepositoryException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response delete(@PathParam("id") UUID ticketID) {
        try {
            this.ticketRepository.delete(ticketID);
            return Response.status(Response.Status.OK).entity("Ticket with given ID was deleted.").build();
        } catch (TicketRepositoryException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }
    }
}
