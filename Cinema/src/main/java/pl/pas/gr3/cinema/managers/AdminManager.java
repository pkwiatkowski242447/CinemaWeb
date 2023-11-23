package pl.pas.gr3.cinema.managers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pl.pas.gr3.cinema.exceptions.repositories.ClientRepositoryException;
import pl.pas.gr3.cinema.model.users.Admin;
import pl.pas.gr3.cinema.model.users.Client;
import pl.pas.gr3.cinema.repositories.implementations.ClientRepository;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
@Path("/admins")
@Named
public class AdminManager extends Manager<Admin> {

    @Inject
    private ClientRepository clientRepository;

    @POST
    @Path("/create")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@QueryParam("login") String adminLogin, @QueryParam("password") String adminPassword) {
        try {
            Admin admin = this.clientRepository.createAdmin(adminLogin, adminPassword);
            return Response.status(Response.Status.CREATED).entity(admin).location(URI.create("/" + admin.getClientID().toString())).build();
        } catch (ClientRepositoryException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    @Override
    public Response findByUUID(@PathParam("id") UUID adminID) {
        try {
            Client admin = this.clientRepository.findByUUID(adminID);
            if (admin == null) {
                return Response.status(Response.Status.NO_CONTENT).build();
            } else {
                return Response.status(Response.Status.OK).entity(admin).build();
            }
        } catch (ClientRepositoryException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }
    }

    @GET
    @Path("/")
    @Override
    public Response findAll() {
        try {
            List<Client> listOfFoundAdmins = this.clientRepository.findAll();
            if (listOfFoundAdmins.isEmpty()) {
                return Response.status(Response.Status.NO_CONTENT).build();
            } else {
                return Response.status(Response.Status.OK).entity(listOfFoundAdmins).build();
            }
        } catch (ClientRepositoryException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }
    }

    @PUT
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Override
    public Response update(Admin admin) {
        try {
            this.clientRepository.update(admin);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (ClientRepositoryException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }
    }

    @POST
    @Path("/{id}/activate")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response activate(@PathParam("id") UUID adminID) {
        try {
            this.clientRepository.activate(this.clientRepository.findByUUID(adminID));
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (ClientRepositoryException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }
    }

    @POST
    @Path("/{id}/deactivate")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response deactivate(@PathParam("id") UUID adminID) {
        try {
            this.clientRepository.deactivate(this.clientRepository.findByUUID(adminID));
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (ClientRepositoryException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }
    }
}
