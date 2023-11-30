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
import pl.pas.gr3.cinema.dto.users.ClientDTO;
import pl.pas.gr3.cinema.dto.users.ClientInputDTO;
import pl.pas.gr3.cinema.dto.users.ClientPasswordDTO;
import pl.pas.gr3.cinema.exceptions.managers.GeneralManagerException;
import pl.pas.gr3.cinema.managers.implementations.ClientManager;
import pl.pas.gr3.cinema.model.Ticket;
import pl.pas.gr3.cinema.model.users.Client;
import pl.pas.gr3.cinema.services.interfaces.UserServiceInterface;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@ApplicationScoped
@Path("/clients")
@Named
public class ClientService implements UserServiceInterface<Client> {

    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Inject
    private ClientManager clientManager;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(ClientInputDTO clientInputDTO) {
        try {
            Client client = this.clientManager.create(clientInputDTO.getClientLogin(), clientInputDTO.getClientPassword());
            Set<ConstraintViolation<Client>> violationSet = validator.validate(client);
            List<String> messages = violationSet.stream().map(ConstraintViolation::getMessage).toList();
            if (!violationSet.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).entity(messages).build();
            }
            ClientDTO clientDTO = new ClientDTO(client.getClientID(), client.getClientLogin(), client.isClientStatusActive());
            return Response.status(Response.Status.CREATED).entity(clientDTO).contentLocation(URI.create("/" + clientDTO.getClientID().toString())).build();
        } catch (GeneralManagerException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }
    }

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response findAll() {
        try {
            List<ClientDTO> listOfDTOs = this.getListOfClientDTOs(this.clientManager.findAll());
            return this.generateResponseForListOfDTOs(listOfDTOs);
        } catch (GeneralManagerException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response findByUUID(@PathParam("id") UUID clientID) {
        try {
            Client client = this.clientManager.findByUUID(clientID);
            ClientDTO clientDTO = new ClientDTO(client.getClientID(), client.getClientLogin(), client.isClientStatusActive());
            return this.generateResponseForDTO(clientDTO);
        } catch (GeneralManagerException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }
    }

    @GET
    @Path("/login/{login}")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response findByLogin(@PathParam("login") String clientLogin) {
        try {
            Client client = this.clientManager.findByLogin(clientLogin);
            ClientDTO clientDTO = new ClientDTO(client.getClientID(), client.getClientLogin(), client.isClientStatusActive());
            return this.generateResponseForDTO(clientDTO);
        } catch (GeneralManagerException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response findAllWithMatchingLogin(@QueryParam("match") String clientLogin) {
        try {
            List<ClientDTO> listOfDTOs = this.getListOfClientDTOs(this.clientManager.findAllMatchingLogin(clientLogin));
            return this.generateResponseForListOfDTOs(listOfDTOs);
        } catch (GeneralManagerException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}/tickets")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response getTicketsForCertainUser(@PathParam("id") UUID clientID) {
        try {
            List<Ticket> listOfTicketsForAClient = this.clientManager.getTicketsForClient(clientID);
            List<TicketDTO> listOfDTOs = new ArrayList<>();
            for (Ticket ticket : listOfTicketsForAClient) {
                listOfDTOs.add(new TicketDTO(ticket.getTicketID(), ticket.getMovieTime(), ticket.getTicketFinalPrice(), ticket.getClient().getClientID(), ticket.getMovie().getMovieID()));
            }
            if (listOfTicketsForAClient.isEmpty()) {
                return Response.status(Response.Status.NO_CONTENT).build();
            } else {
                return Response.status(Response.Status.OK).entity(listOfDTOs).build();
            }
        } catch (GeneralManagerException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(ClientPasswordDTO clientPasswordDTO) {
        try {
            Client client = new Client(clientPasswordDTO.getClientID(), clientPasswordDTO.getClientLogin(), clientPasswordDTO.getClientPassword(), clientPasswordDTO.isClientStatusActive());
            Set<ConstraintViolation<Client>> violationSet = validator.validate(client);
            List<String> messages = violationSet.stream().map(ConstraintViolation::getMessage).toList();
            if (!violationSet.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON).entity(messages).build();
            }
            this.clientManager.update(client);
            return Response.status(Response.Status.NO_CONTENT).type(MediaType.APPLICATION_JSON).build();
        } catch (GeneralManagerException exception) {
            return Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON).entity(exception.getMessage()).build();
        }
    }

    @POST
    @Path("/{id}/activate")
    @Override
    public Response activate(@PathParam("id") UUID clientID) {
        try {
            this.clientManager.activate(clientID);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (GeneralManagerException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }
    }

    @POST
    @Path("/{id}/deactivate")
    @Override
    public Response deactivate(@PathParam("id") UUID clientID) {
        try {
            this.clientManager.deactivate(clientID);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (GeneralManagerException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }
    }

    private List<ClientDTO> getListOfClientDTOs(List<Client> listOfClients) {
        List<ClientDTO> listOfDTOs = new ArrayList<>();
        for (Client client : listOfClients) {
            listOfDTOs.add(new ClientDTO(client.getClientID(), client.getClientLogin(), client.isClientStatusActive()));
        }
        return listOfDTOs;
    }

    private Response generateResponseForDTO(ClientDTO clientDTO) {
        if (clientDTO == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            return Response.status(Response.Status.OK).entity(clientDTO).build();
        }
    }

    private Response generateResponseForListOfDTOs(List<ClientDTO> listOfDTOs) {
        if (listOfDTOs.isEmpty()) {
            return Response.status(Response.Status.NO_CONTENT).build();
        } else {
            return Response.status(Response.Status.OK).entity(listOfDTOs).build();
        }
    }
}
