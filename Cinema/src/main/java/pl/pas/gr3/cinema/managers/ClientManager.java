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
import pl.pas.gr3.cinema.dto.users.ClientDTO;
import pl.pas.gr3.cinema.exceptions.repositories.ClientRepositoryException;
import pl.pas.gr3.cinema.model.Ticket;
import pl.pas.gr3.cinema.model.users.Client;
import pl.pas.gr3.cinema.repositories.implementations.ClientRepository;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@ApplicationScoped
@Path("/clients")
@Named
public class ClientManager extends Manager<Client> {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Inject
    private ClientRepository clientRepository;

    @POST
    @Path("/create")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@QueryParam("login") String clientLogin, @QueryParam("password") String clientPassword) {
        try {
            Client client = this.clientRepository.createClient(clientLogin, clientPassword);
            Set<ConstraintViolation<Client>> violationSet = validator.validate(client);
            List<String> messages = violationSet.stream().map(ConstraintViolation::getMessage).toList();
            if (!violationSet.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).entity(messages).build();
            }
            ClientDTO clientDTO = new ClientDTO(client.getClientID(), client.getClientLogin(), client.isClientStatusActive());
            return Response.status(Response.Status.CREATED).entity(clientDTO).location(URI.create("/" + clientDTO.getClientID().toString())).build();
        } catch (ClientRepositoryException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response findByUUID(@PathParam("id") UUID clientID) {
        try {
            Client client = this.clientRepository.findByUUID(clientID);
            ClientDTO clientDTO = new ClientDTO(client.getClientID(), client.getClientLogin(), client.isClientStatusActive());
            return this.generateResponseForDTO(clientDTO);
        } catch (ClientRepositoryException exception) {
            return Response.status(Response.Status.NOT_FOUND).entity(exception.getMessage()).build();
        }
    }

    @GET
    @Path("/")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response findByLogin(@QueryParam("login") String clientLogin) {
        try {
            Client client = this.clientRepository.findClientByLogin(clientLogin);
            ClientDTO clientDTO = new ClientDTO(client.getClientID(), client.getClientLogin(), client.isClientStatusActive());
            return this.generateResponseForDTO(clientDTO);
        } catch (ClientRepositoryException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }
    }

    @GET
    @Path("/")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAllWithMatchingLogin(@QueryParam("match") String clientLogin) {
        try {
            List<ClientDTO> listOfDTOs = this.getListOfClientDTOs(this.clientRepository.findAllStaffsMatchingLogin(clientLogin));
            return this.generateResponseForListOfDTOs(listOfDTOs);
        } catch (ClientRepositoryException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}/tickets")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTicketsForCertainClient(@PathParam("id") UUID clientID) {
        List<Ticket> listOfTicketsForAClient = this.clientRepository.getListOfTicketsForClient(clientID);
        List<TicketDTO> listOfDTOs = new ArrayList<>();
        for (Ticket ticket : listOfTicketsForAClient) {
            listOfDTOs.add(new TicketDTO(ticket.getTicketID(), ticket.getMovieTime(), ticket.getTicketFinalPrice(), ticket.getClient().getClientID(), ticket.getMovie().getMovieID()));
        }
        if (listOfTicketsForAClient.isEmpty()) {
            return Response.status(Response.Status.NO_CONTENT).build();
        } else {
            return Response.status(Response.Status.OK).entity(listOfDTOs).build();
        }
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response findAll() {
        try {
            List<ClientDTO> listOfDTOs = this.getListOfClientDTOs(this.clientRepository.findAllClients());
            return this.generateResponseForListOfDTOs(listOfDTOs);
        } catch (ClientRepositoryException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }
    }

    @PUT
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Override
    public Response update(Client client) {
        try {
            this.clientRepository.updateClient(client);
            return Response.status(Response.Status.OK).entity("Client object was modified.").build();
        } catch (ClientRepositoryException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }
    }

    @POST
    @Path("/{id}/activate")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response activate(@PathParam("id") UUID clientID) {
        try {
            this.clientRepository.activate(this.clientRepository.findByUUID(clientID));
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (ClientRepositoryException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }
    }

    @POST
    @Path("/{id}/deactivate")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response deactivate(@PathParam("id") UUID clientID) {
        try {
            this.clientRepository.deactivate(this.clientRepository.findByUUID(clientID));
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (ClientRepositoryException exception) {
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
            return Response.status(Response.Status.NO_CONTENT).build();
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
