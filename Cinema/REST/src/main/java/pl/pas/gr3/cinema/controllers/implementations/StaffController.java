package pl.pas.gr3.cinema.controllers.implementations;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pas.gr3.cinema.exceptions.services.crud.staff.StaffServiceCreateStaffDuplicateLoginException;
import pl.pas.gr3.cinema.exceptions.services.crud.staff.StaffServiceStaffNotFoundException;
import pl.pas.gr3.dto.TicketDTO;
import pl.pas.gr3.dto.users.StaffDTO;
import pl.pas.gr3.dto.users.StaffInputDTO;
import pl.pas.gr3.dto.users.StaffPasswordDTO;
import pl.pas.gr3.cinema.exceptions.services.GeneralServiceException;
import pl.pas.gr3.cinema.services.implementations.StaffService;
import pl.pas.gr3.cinema.model.Ticket;
import pl.pas.gr3.cinema.model.users.Client;
import pl.pas.gr3.cinema.model.users.Staff;
import pl.pas.gr3.cinema.controllers.interfaces.UserServiceInterface;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/staffs")
public class StaffController implements UserServiceInterface<Staff> {

    private final static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private final StaffService staffService;

    @Autowired
    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> create(@RequestBody StaffInputDTO staffInputDTO) {
        try {
            Staff staff = this.staffService.create(staffInputDTO.getStaffLogin(), staffInputDTO.getStaffPassword());

            Set<ConstraintViolation<Staff>> violationSet = validator.validate(staff);
            List<String> messages = violationSet.stream().map(ConstraintViolation::getMessage).toList();
            if (!violationSet.isEmpty()) {
                return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(messages);
            }

            StaffDTO staffDTO = new StaffDTO(staff.getClientID(), staff.getClientLogin(), staff.isClientStatusActive());
            return ResponseEntity.created(URI.create("http://localhost:8000/api/v1/staffs/" + staffDTO.getStaffID().toString())).contentType(MediaType.APPLICATION_JSON).body(staffDTO);
        } catch (StaffServiceCreateStaffDuplicateLoginException exception) {
            return ResponseEntity.status(HttpStatus.CONFLICT).contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<?> findByUUID(@PathVariable("id") UUID staffID) {
        try {
            Client staff = this.staffService.findByUUID(staffID);
            StaffDTO staffDTO = new StaffDTO(staff.getClientID(), staff.getClientLogin(), staff.isClientStatusActive());
            return this.generateResponseForDTO(staffDTO);
        } catch (StaffServiceStaffNotFoundException exception) {
            return ResponseEntity.notFound().build();
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @GetMapping(value = "/login/{login}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<?> findByLogin(@PathVariable("login") String staffLogin) {
        try {
            Staff staff = this.staffService.findByLogin(staffLogin);
            StaffDTO staffDTO = new StaffDTO(staff.getClientID(), staff.getClientLogin(), staff.isClientStatusActive());
            return this.generateResponseForDTO(staffDTO);
        } catch (StaffServiceStaffNotFoundException exception) {
            return ResponseEntity.notFound().build();
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<?> findAllWithMatchingLogin(@RequestParam("match") String staffLogin) {
        try {
            List<StaffDTO> listOfDTOs = this.getListOfStaffDTOs(this.staffService.findAllMatchingLogin(staffLogin));
            return this.generateResponseForListOfDTOs(listOfDTOs);
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @GetMapping(value = "/{id}/ticket-list", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<?> getTicketsForCertainUser(@PathVariable("id") UUID staffID) {
        try {
            List<Ticket> listOfTicketForAStaff = this.staffService.getTicketsForClient(staffID);
            List<TicketDTO> listOfDTOs = new ArrayList<>();
            for (Ticket ticket : listOfTicketForAStaff) {
                listOfDTOs.add(new TicketDTO(ticket.getTicketID(), ticket.getMovieTime(), ticket.getTicketFinalPrice(), ticket.getClient().getClientID(), ticket.getMovie().getMovieID()));
            }
            if (listOfTicketForAStaff.isEmpty()) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(listOfDTOs);
            }
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<?> findAll() {
        try {
            List<StaffDTO> listOfDTOs = this.getListOfStaffDTOs(this.staffService.findAll());
            return this.generateResponseForListOfDTOs(listOfDTOs);
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@RequestBody StaffPasswordDTO staffPasswordDTO) {
        try {
            Staff staff = new Staff(staffPasswordDTO.getStaffID(), staffPasswordDTO.getStaffLogin(), staffPasswordDTO.getStaffPassword(), staffPasswordDTO.isStaffStatusActive());

            Set<ConstraintViolation<Staff>> violationSet = validator.validate(staff);
            List<String> messages = violationSet.stream().map(ConstraintViolation::getMessage).toList();
            if (!violationSet.isEmpty()) {
                return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(messages);
            }

            this.staffService.update(staff);
            return ResponseEntity.noContent().build();
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @PostMapping(value = "/{id}/activate")
    @Override
    public ResponseEntity<?> activate(@PathVariable("id") UUID staffID) {
        try {
            this.staffService.activate(staffID);
            return ResponseEntity.noContent().build();
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @PostMapping(value = "/{id}/deactivate")
    @Override
    public ResponseEntity<?> deactivate(@PathVariable("id") UUID staffID) {
        try {
            this.staffService.deactivate(staffID);
            return ResponseEntity.noContent().build();
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    private List<StaffDTO> getListOfStaffDTOs(List<Staff> listOfClients) {
        List<StaffDTO> listOfDTOs = new ArrayList<>();
        for (Client staff : listOfClients) {
            listOfDTOs.add(new StaffDTO(staff.getClientID(), staff.getClientLogin(), staff.isClientStatusActive()));
        }
        return listOfDTOs;
    }

    private ResponseEntity<?> generateResponseForDTO(StaffDTO staffDTO) {
        if (staffDTO == null) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(staffDTO);
        }
    }

    private ResponseEntity<?> generateResponseForListOfDTOs(List<StaffDTO> listOfDTOs) {
        if (listOfDTOs.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(listOfDTOs);
        }
    }
}
