package pl.pas.gr3.cinema.managers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.annotation.RequestParameterMap;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pl.pas.gr3.cinema.dto.TicketDTO;
import pl.pas.gr3.cinema.dto.users.AdminDTO;
import pl.pas.gr3.cinema.dto.users.AdminTestDTO;
import pl.pas.gr3.cinema.exceptions.repositories.ClientRepositoryException;
import pl.pas.gr3.cinema.model.Ticket;
import pl.pas.gr3.cinema.model.users.Admin;
import pl.pas.gr3.cinema.model.users.Client;
import pl.pas.gr3.cinema.repositories.implementations.ClientRepository;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@ApplicationScoped
@Path("/admins")
@Named
public class AdminManager extends Manager<Admin> {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    @Inject
    private ClientRepository clientRepository;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(AdminTestDTO adminTestDTO) {
        try {
            Admin admin = this.clientRepository.createAdmin(adminTestDTO.getAdminLogin(), adminTestDTO.getAdminPassword());
            Set<ConstraintViolation<Admin>> violationSet = validator.validate(admin);
            List<String> messages = violationSet.stream().map(ConstraintViolation::getMessage).toList();
            if (!violationSet.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON).entity(messages).build();
            }
            AdminDTO adminDTO = new AdminDTO(admin.getClientID(), admin.getClientLogin(), admin.isClientStatusActive());
            return Response.status(Response.Status.CREATED).type(MediaType.APPLICATION_JSON).entity(adminDTO).contentLocation(URI.create("/" + adminDTO.getAdminID().toString())).build();
        } catch (ClientRepositoryException exception) {
            return Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON).entity(exception.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response findByUUID(@PathParam("id") UUID adminID) {
        try {
            Client admin = this.clientRepository.findByUUID(adminID);
            AdminDTO adminDTO = new AdminDTO(admin.getClientID(), admin.getClientLogin(), admin.isClientStatusActive());
            return this.generateResponseForDTO(adminDTO);
        } catch (ClientRepositoryException exception) {
            return Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON).entity(exception.getMessage()).build();
        }
    }

    @GET
    @Path("/login/{login}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findByLogin(@PathParam("login") String adminLogin) {
        try {
            Client admin = this.clientRepository.findAdminByLogin(adminLogin);
            AdminDTO adminDTO = new AdminDTO(admin.getClientID(), admin.getClientLogin(), admin.isClientStatusActive());
            return this.generateResponseForDTO(adminDTO);
        } catch (ClientRepositoryException exception) {
            return Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON).entity(exception.getMessage()).build();
        }
    }

    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAllWithMatchingLogin(@QueryParam("match") String adminLogin) {
        try {
            List<AdminDTO> listOfDTOs = this.getListOfAdminDTOs(this.clientRepository.findAllAdminsMatchingLogin(adminLogin));
            return this.generateResponseForListOfDTOs(listOfDTOs);
        } catch (ClientRepositoryException exception) {
            return Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON).entity(exception.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}/tickets")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTicketsForCertainAdmin(@PathParam("id") UUID adminID) {
        List<Ticket> listOfTicketsForAnAdmin = this.clientRepository.getListOfTicketsForClient(adminID);
        List<TicketDTO> listOfDTOs = new ArrayList<>();
        for (Ticket ticket : listOfTicketsForAnAdmin) {
            listOfDTOs.add(new TicketDTO(ticket.getTicketID(), ticket.getMovieTime(), ticket.getTicketFinalPrice(), ticket.getClient().getClientID(), ticket.getMovie().getMovieID()));
        }
        if (listOfTicketsForAnAdmin.isEmpty()) {
            return Response.status(Response.Status.NO_CONTENT).type(MediaType.APPLICATION_JSON).build();
        } else {
            return Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON).entity(listOfDTOs).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response findAll() {
        try {
            List<Client> listOfAdmins = this.clientRepository.findAllAdmins();
            List<AdminDTO> listOfDTOs = this.getListOfAdminDTOs(listOfAdmins);
            return this.generateResponseForListOfDTOs(listOfDTOs);
        } catch (ClientRepositoryException exception) {
            return Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON).entity(exception.getMessage()).build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Override
    public Response update(Admin admin) {
        try {
            this.clientRepository.updateAdmin(admin);
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

    private List<AdminDTO> getListOfAdminDTOs(List<Client> listOfAdmins) {
        List<AdminDTO> listOfDTOs = new ArrayList<>();
        for (Client admin : listOfAdmins) {
            listOfDTOs.add(new AdminDTO(admin.getClientID(), admin.getClientLogin(), admin.isClientStatusActive()));
        }
        return listOfDTOs;
    }
}
