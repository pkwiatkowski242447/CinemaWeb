package pl.pas.gr3.cinema.controllers.implementations;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.pas.gr3.cinema.dto.TicketDTO;
import pl.pas.gr3.cinema.dto.users.AdminDTO;
import pl.pas.gr3.cinema.dto.users.AdminInputDTO;
import pl.pas.gr3.cinema.dto.users.AdminPasswordDTO;
import pl.pas.gr3.cinema.exceptions.managers.GeneralManagerException;
import pl.pas.gr3.cinema.managers.implementations.AdminManager;
import pl.pas.gr3.cinema.model.Ticket;
import pl.pas.gr3.cinema.model.users.Admin;
import pl.pas.gr3.cinema.controllers.interfaces.UserServiceInterface;

import java.net.URI;
import java.util.*;


@ApplicationScoped
@Path("/admins")
@Named
public class AdminController implements UserServiceInterface<Admin> {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    @Inject
    private AdminManager adminManager;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(AdminInputDTO adminInputDTO) {
        try {
            Admin admin = this.adminManager.create(adminInputDTO.getAdminLogin(), adminInputDTO.getAdminPassword());
            Set<ConstraintViolation<Admin>> violationSet = validator.validate(admin);
            List<String> messages = violationSet.stream().map(ConstraintViolation::getMessage).toList();
            if (!violationSet.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON).entity(messages).build();
            }
            AdminDTO adminDTO = new AdminDTO(admin.getClientID(), admin.getClientLogin(), admin.isClientStatusActive());
            return Response.status(Response.Status.CREATED).type(MediaType.APPLICATION_JSON).entity(adminDTO).contentLocation(URI.create("/" + adminDTO.getAdminID().toString())).build();
        } catch (GeneralManagerException exception) {
            return Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON).entity(exception.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response findByUUID(@PathParam("id") UUID adminID) {
        try {
            Admin admin = this.adminManager.findByUUID(adminID);
            AdminDTO adminDTO = new AdminDTO(admin.getClientID(), admin.getClientLogin(), admin.isClientStatusActive());
            return this.generateResponseForDTO(adminDTO);
        } catch (GeneralManagerException exception) {
            return Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON).entity(exception.getMessage()).build();
        }
    }

    @GET
    @Path("/login/{login}")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response findByLogin(@PathParam("login") String adminLogin) {
        try {
            Admin admin = this.adminManager.findByLogin(adminLogin);
            AdminDTO adminDTO = new AdminDTO(admin.getClientID(), admin.getClientLogin(), admin.isClientStatusActive());
            return this.generateResponseForDTO(adminDTO);
        } catch (GeneralManagerException exception) {
            return Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON).entity(exception.getMessage()).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response findAllWithMatchingLogin(@QueryParam("match") String adminLogin) {
        try {
            List<AdminDTO> listOfDTOs = this.getListOfAdminDTOs(this.adminManager.findAllMatchingLogin(adminLogin));
            return this.generateResponseForListOfDTOs(listOfDTOs);
        } catch (GeneralManagerException exception) {
            return Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON).entity(exception.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}/ticket-list")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response getTicketsForCertainUser(@PathParam("id") UUID adminID) {
        try {
            List<Ticket> listOfTicketsForAnAdmin = this.adminManager.getTicketsForClient(adminID);
            List<TicketDTO> listOfDTOs = new ArrayList<>();
            for (Ticket ticket : listOfTicketsForAnAdmin) {
                listOfDTOs.add(new TicketDTO(ticket.getTicketID(), ticket.getMovieTime(), ticket.getTicketFinalPrice(), ticket.getClient().getClientID(), ticket.getMovie().getMovieID()));
            }
            if (listOfTicketsForAnAdmin.isEmpty()) {
                return Response.status(Response.Status.NO_CONTENT).type(MediaType.APPLICATION_JSON).build();
            } else {
                return Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON).entity(listOfDTOs).build();
            }
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
            List<Admin> listOfAdmins = this.adminManager.findAll();
            List<AdminDTO> listOfDTOs = this.getListOfAdminDTOs(listOfAdmins);
            return this.generateResponseForListOfDTOs(listOfDTOs);
        } catch (GeneralManagerException exception) {
            return Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON).entity(exception.getMessage()).build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(AdminPasswordDTO adminPasswordDTO) {
        try {
            Admin admin = new Admin(adminPasswordDTO.getAdminID(), adminPasswordDTO.getAdminLogin(), adminPasswordDTO.getAdminPassword(), adminPasswordDTO.isAdminStatusActive());
            Set<ConstraintViolation<Admin>> violationSet = validator.validate(admin);
            List<String> messages = violationSet.stream().map(ConstraintViolation::getMessage).toList();
            if (!violationSet.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON).entity(messages).build();
            }
            this.adminManager.update(admin);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (GeneralManagerException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }
    }

    @POST
    @Path("/{id}/activate")
    @Override
    public Response activate(@PathParam("id") UUID adminID) {
        try {
            this.adminManager.activate(adminID);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (GeneralManagerException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }
    }

    @POST
    @Path("/{id}/deactivate")
    @Override
    public Response deactivate(@PathParam("id") UUID adminID) {
        try {
            this.adminManager.deactivate(adminID);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (GeneralManagerException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }
    }

    private Response generateResponseForDTO(AdminDTO adminDTO) {
        if (adminDTO == null) {
            return Response.status(Response.Status.NO_CONTENT).type(MediaType.APPLICATION_JSON).build();
        } else {
            return Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON).entity(adminDTO).build();
        }
    }

    private Response generateResponseForListOfDTOs(List<AdminDTO> listOfDTOs) {
        if (listOfDTOs.isEmpty()) {
            return Response.status(Response.Status.NO_CONTENT).type(MediaType.APPLICATION_JSON).build();
        } else {
            return Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON).entity(listOfDTOs).build();
        }
    }

    private List<AdminDTO> getListOfAdminDTOs(List<Admin> listOfAdmins) {
        List<AdminDTO> listOfDTOs = new ArrayList<>();
        for (Admin admin : listOfAdmins) {
            listOfDTOs.add(new AdminDTO(admin.getClientID(), admin.getClientLogin(), admin.isClientStatusActive()));
        }
        return listOfDTOs;
    }
}
