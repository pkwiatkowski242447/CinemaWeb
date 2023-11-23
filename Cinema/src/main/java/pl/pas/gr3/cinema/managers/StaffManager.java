package pl.pas.gr3.cinema.managers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pl.pas.gr3.cinema.exceptions.repositories.ClientRepositoryException;
import pl.pas.gr3.cinema.model.users.Client;
import pl.pas.gr3.cinema.model.users.Admin;
import pl.pas.gr3.cinema.model.users.Staff;
import pl.pas.gr3.cinema.repositories.implementations.ClientRepository;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
@Path("/staffs")
@Named
public class StaffManager extends Manager<Admin> {

    @Inject
    private ClientRepository clientRepository;

    @POST
    @Path("/create")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@QueryParam("login") String staffLogin, @QueryParam("password") String staffPassword) {
        try {
            Staff staff = this.clientRepository.createStaff(staffLogin, staffPassword);
            return Response.status(Response.Status.CREATED).entity(staff).build();
        } catch (ClientRepositoryException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response findByUUID(@PathParam("id") UUID staffID) {
        try {
            Client staff = this.clientRepository.findByUUID(staffID);
            if (staff == null) {
                return Response.status(Response.Status.NO_CONTENT).build();
            } else {
                return Response.status(Response.Status.OK).entity(staff).build();
            }
        } catch (ClientRepositoryException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }
    }

    @GET
    @Path("/")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response findAll() {
        try {
            List<Client> listOfFoundStaff = this.clientRepository.findAll();
            if (listOfFoundStaff.isEmpty()) {
                return Response.status(Response.Status.NO_CONTENT).build();
            } else {
                return Response.status(Response.Status.OK).entity(listOfFoundStaff).build();
            }
        } catch (ClientRepositoryException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }
    }

    @PUT
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Override
    public Response update(Admin staff) {
        try {
            this.clientRepository.update(staff);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (ClientRepositoryException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }
    }

    @POST
    @Path("/{id}/activate")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response activate(@PathParam("id") UUID staffID) {
        try {
            this.clientRepository.activate(this.clientRepository.findByUUID(staffID));
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (ClientRepositoryException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }
    }

    @POST
    @Path("/{id}/deactivate")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response deactivate(@PathParam("id") UUID staffID) {
        try {
            this.clientRepository.deactivate(this.clientRepository.findByUUID(staffID));
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (ClientRepositoryException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }
    }
}
