package pl.pas.gr3.cinema.managers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pl.pas.gr3.cinema.exceptions.repositories.ClientRepositoryException;
import pl.pas.gr3.cinema.model.users.Client;
import pl.pas.gr3.cinema.repositories.implementations.ClientRepository;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
@Path("/clients")
@Named
public class ClientManager extends Manager<Client> {

    @Inject
    private ClientRepository clientRepository;

    @POST
    @Path("/create")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@QueryParam("login") String clientLogin, @QueryParam("password") String clientPassword) {
        try {
            Client client = this.clientRepository.createClient(clientLogin, clientPassword);
            return Response.status(Response.Status.CREATED).entity(client).location(URI.create("/" + client.getClientID().toString())).build();
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
            if (client == null) {
                return Response.status(Response.Status.NO_CONTENT).build();
            } else {
                return Response.status(Response.Status.OK).entity(client).build();
            }
        } catch (ClientRepositoryException exception) {
            return Response.status(Response.Status.NOT_FOUND).entity(exception.getMessage()).build();
        }
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response findAll() {
        try {
            List<Client> listOfFoundClients = this.clientRepository.findAll();
            if (listOfFoundClients.isEmpty()) {
                return Response.status(Response.Status.NO_CONTENT).build();
            } else {
                return Response.status(Response.Status.OK).entity(listOfFoundClients).build();
            }
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
            this.clientRepository.update(client);
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

    @GET
    @Path("/test")
    @Produces(MediaType.TEXT_PLAIN)
    public Response helloWorld() {
        return Response.status(Response.Status.OK).entity("Super jest.").build();
    }
}
