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
import pl.pas.gr3.cinema.dto.users.StaffDTO;
import pl.pas.gr3.cinema.exceptions.repositories.ClientRepositoryException;
import pl.pas.gr3.cinema.model.Ticket;
import pl.pas.gr3.cinema.model.users.Client;
import pl.pas.gr3.cinema.model.users.Staff;
import pl.pas.gr3.cinema.repositories.implementations.ClientRepository;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@ApplicationScoped
@Path("/staffs")
@Named
public class StaffManager extends Manager<Staff> {

    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Inject
    private ClientRepository clientRepository;

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@QueryParam("login") String staffLogin, @QueryParam("password") String staffPassword) {
        try {
            Staff staff = this.clientRepository.createStaff(staffLogin, staffPassword);
            Set<ConstraintViolation<Staff>> violationSet = validator.validate(staff);
            List<String> messages = violationSet.stream().map(ConstraintViolation::getMessage).toList();
            if (!violationSet.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).entity(messages).build();
            }
            StaffDTO staffDTO = new StaffDTO(staff.getClientID(), staff.getClientLogin(), staff.isClientStatusActive());
            return Response.status(Response.Status.CREATED).contentLocation(URI.create("/" + staffDTO.getStaffID().toString())).entity(staffDTO).build();
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
            StaffDTO staffDTO = new StaffDTO(staff.getClientID(), staff.getClientLogin(), staff.isClientStatusActive());
            return this.generateResponseForDTO(staffDTO);
        } catch (ClientRepositoryException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }
    }

    @GET
    @Path("/login/{login}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response findByLogin(@PathParam("login") String staffLogin) {
        try {
            Staff staff = this.clientRepository.findStaffByLogin(staffLogin);
            StaffDTO staffDTO = new StaffDTO(staff.getClientID(), staff.getClientLogin(), staff.isClientStatusActive());
            return this.generateResponseForDTO(staffDTO);
        } catch (ClientRepositoryException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }
    }

    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAllWithMatchingLogin(@QueryParam("match") String staffLogin) {
        try {
            List<StaffDTO> listOfDTOs = this.getListOfStaffDTOs(this.clientRepository.findAllStaffsMatchingLogin(staffLogin));
            return this.generateResponseForListOfDTOs(listOfDTOs);
        } catch (ClientRepositoryException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}/tickets")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTicketsForCertainStaff(@PathParam("id") UUID staffID) {
        List<Ticket> listOfTicketForAStaff = this.clientRepository.getListOfTicketsForClient(staffID);
        List<TicketDTO> listOfDTOs = new ArrayList<>();
        for (Ticket ticket : listOfTicketForAStaff) {
            listOfDTOs.add(new TicketDTO(ticket.getTicketID(), ticket.getMovieTime(), ticket.getTicketFinalPrice(), ticket.getClient().getClientID(), ticket.getMovie().getMovieID()));
        }
        if (listOfTicketForAStaff.isEmpty()) {
            return Response.status(Response.Status.NO_CONTENT).build();
        } else {
            return Response.status(Response.Status.OK).entity(listOfDTOs).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response findAll() {
        try {
            List<StaffDTO> listOfDTOs = this.getListOfStaffDTOs(this.clientRepository.findAllStaffs());
            return this.generateResponseForListOfDTOs(listOfDTOs);
        } catch (ClientRepositoryException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Override
    public Response update(Staff staff) {
        try {
            this.clientRepository.updateStaff(staff);
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

    private List<StaffDTO> getListOfStaffDTOs(List<Client> listOfClients) {
        List<StaffDTO> listOfDTOs = new ArrayList<>();
        for (Client staff : listOfClients) {
            listOfDTOs.add(new StaffDTO(staff.getClientID(), staff.getClientLogin(), staff.isClientStatusActive()));
        }
        return listOfDTOs;
    }
}
