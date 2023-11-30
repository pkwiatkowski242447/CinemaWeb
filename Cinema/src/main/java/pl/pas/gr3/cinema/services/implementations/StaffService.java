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
import pl.pas.gr3.cinema.dto.users.StaffDTO;
import pl.pas.gr3.cinema.dto.users.StaffInputDTO;
import pl.pas.gr3.cinema.dto.users.StaffPasswordDTO;
import pl.pas.gr3.cinema.exceptions.managers.GeneralManagerException;
import pl.pas.gr3.cinema.managers.implementations.StaffManager;
import pl.pas.gr3.cinema.model.Ticket;
import pl.pas.gr3.cinema.model.users.Client;
import pl.pas.gr3.cinema.model.users.Staff;
import pl.pas.gr3.cinema.services.interfaces.UserServiceInterface;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@ApplicationScoped
@Path("/staffs")
@Named
public class StaffService implements UserServiceInterface<Staff> {

    private final static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Inject
    private StaffManager staffManager;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(StaffInputDTO staffInputDTO) {
        try {
            Staff staff = this.staffManager.create(staffInputDTO.getStaffLogin(), staffInputDTO.getStaffPassword());
            Set<ConstraintViolation<Staff>> violationSet = validator.validate(staff);
            List<String> messages = violationSet.stream().map(ConstraintViolation::getMessage).toList();
            if (!violationSet.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).entity(messages).build();
            }
            StaffDTO staffDTO = new StaffDTO(staff.getClientID(), staff.getClientLogin(), staff.isClientStatusActive());
            return Response.status(Response.Status.CREATED).contentLocation(URI.create("/" + staffDTO.getStaffID().toString())).entity(staffDTO).build();
        } catch (GeneralManagerException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response findByUUID(@PathParam("id") UUID staffID) {
        try {
            Client staff = this.staffManager.findByUUID(staffID);
            StaffDTO staffDTO = new StaffDTO(staff.getClientID(), staff.getClientLogin(), staff.isClientStatusActive());
            return this.generateResponseForDTO(staffDTO);
        } catch (GeneralManagerException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }
    }

    @GET
    @Path("/login/{login}")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response findByLogin(@PathParam("login") String staffLogin) {
        try {
            Staff staff = this.staffManager.findByLogin(staffLogin);
            StaffDTO staffDTO = new StaffDTO(staff.getClientID(), staff.getClientLogin(), staff.isClientStatusActive());
            return this.generateResponseForDTO(staffDTO);
        } catch (GeneralManagerException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response findAllWithMatchingLogin(@QueryParam("match") String staffLogin) {
        try {
            List<StaffDTO> listOfDTOs = this.getListOfStaffDTOs(this.staffManager.findAllMatchingLogin(staffLogin));
            return this.generateResponseForListOfDTOs(listOfDTOs);
        } catch (GeneralManagerException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}/tickets")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response getTicketsForCertainUser(@PathParam("id") UUID staffID) {
        try {
            List<Ticket> listOfTicketForAStaff = this.staffManager.getTicketsForClient(staffID);
            List<TicketDTO> listOfDTOs = new ArrayList<>();
            for (Ticket ticket : listOfTicketForAStaff) {
                listOfDTOs.add(new TicketDTO(ticket.getTicketID(), ticket.getMovieTime(), ticket.getTicketFinalPrice(), ticket.getClient().getClientID(), ticket.getMovie().getMovieID()));
            }
            if (listOfTicketForAStaff.isEmpty()) {
                return Response.status(Response.Status.NO_CONTENT).build();
            } else {
                return Response.status(Response.Status.OK).entity(listOfDTOs).build();
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
            List<StaffDTO> listOfDTOs = this.getListOfStaffDTOs(this.staffManager.findAll());
            return this.generateResponseForListOfDTOs(listOfDTOs);
        } catch (GeneralManagerException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(StaffPasswordDTO staffPasswordDTO) {
        try {
            Staff staff = new Staff(staffPasswordDTO.getStaffID(), staffPasswordDTO.getStaffLogin(), staffPasswordDTO.getStaffPassword(), staffPasswordDTO.isStaffStatusActive());
            Set<ConstraintViolation<Staff>> violationSet = validator.validate(staff);
            List<String> messages = violationSet.stream().map(ConstraintViolation::getMessage).toList();
            if (!violationSet.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON).entity(messages).build();
            }
            this.staffManager.update(staff);
            return Response.status(Response.Status.NO_CONTENT).type(MediaType.APPLICATION_JSON).build();
        } catch (GeneralManagerException exception) {
            return Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON).entity(exception.getMessage()).build();
        }
    }

    @POST
    @Path("/{id}/activate")
    @Override
    public Response activate(@PathParam("id") UUID staffID) {
        try {
            this.staffManager.activate(staffID);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (GeneralManagerException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }
    }

    @POST
    @Path("/{id}/deactivate")
    @Override
    public Response deactivate(@PathParam("id") UUID staffID) {
        try {
            this.staffManager.deactivate(staffID);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (GeneralManagerException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }
    }

    private Response generateResponseForDTO(StaffDTO staffDTO) {
        if (staffDTO == null) {
            return Response.status(Response.Status.NO_CONTENT).build();
        } else {
            return Response.status(Response.Status.OK).entity(staffDTO).build();
        }
    }

    private Response generateResponseForListOfDTOs(List<StaffDTO> listOfDTOs) {
        if (listOfDTOs.isEmpty()) {
            return Response.status(Response.Status.NO_CONTENT).build();
        } else {
            return Response.status(Response.Status.OK).entity(listOfDTOs).build();
        }
    }

    private List<StaffDTO> getListOfStaffDTOs(List<Staff> listOfClients) {
        List<StaffDTO> listOfDTOs = new ArrayList<>();
        for (Client staff : listOfClients) {
            listOfDTOs.add(new StaffDTO(staff.getClientID(), staff.getClientLogin(), staff.isClientStatusActive()));
        }
        return listOfDTOs;
    }
}
