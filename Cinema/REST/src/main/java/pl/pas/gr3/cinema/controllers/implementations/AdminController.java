package pl.pas.gr3.cinema.controllers.implementations;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pas.gr3.cinema.exceptions.services.crud.admin.AdminServiceAdminNotFoundException;
import pl.pas.gr3.cinema.exceptions.services.crud.admin.AdminServiceCreateAdminDuplicateLoginException;
import pl.pas.gr3.dto.TicketDTO;
import pl.pas.gr3.dto.users.AdminDTO;
import pl.pas.gr3.dto.users.AdminInputDTO;
import pl.pas.gr3.dto.users.AdminPasswordDTO;
import pl.pas.gr3.cinema.exceptions.services.GeneralServiceException;
import pl.pas.gr3.cinema.services.implementations.AdminService;
import pl.pas.gr3.cinema.model.Ticket;
import pl.pas.gr3.cinema.model.users.Admin;
import pl.pas.gr3.cinema.controllers.interfaces.UserServiceInterface;

import java.net.URI;
import java.util.*;


@RestController
@RequestMapping("/api/v1/admins")
public class AdminController implements UserServiceInterface<Admin> {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> create(@RequestBody AdminInputDTO adminInputDTO) {
        try {
            Admin admin = this.adminService.create(adminInputDTO.getAdminLogin(), adminInputDTO.getAdminPassword());

            Set<ConstraintViolation<Admin>> violationSet = validator.validate(admin);
            List<String> messages = violationSet.stream().map(ConstraintViolation::getMessage).toList();
            if (!violationSet.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body(messages);
            }

            AdminDTO adminDTO = new AdminDTO(admin.getClientID(), admin.getClientLogin(), admin.isClientStatusActive());
            return ResponseEntity.created(URI.create("/" + adminDTO.getAdminID().toString())).contentType(MediaType.APPLICATION_JSON).body(adminDTO);
        } catch (AdminServiceCreateAdminDuplicateLoginException exception) {
            return ResponseEntity.status(HttpStatus.CONFLICT).contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<?> findByUUID(@PathVariable("id") UUID adminID) {
        try {
            Admin admin = this.adminService.findByUUID(adminID);
            AdminDTO adminDTO = new AdminDTO(admin.getClientID(), admin.getClientLogin(), admin.isClientStatusActive());
            return this.generateResponseEntityForDTO(adminDTO);
        } catch (AdminServiceAdminNotFoundException exception) {
            return ResponseEntity.notFound().build();
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @GetMapping(value = "/login/{login}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<?> findByLogin(@PathVariable("login") String adminLogin) {
        try {
            Admin admin = this.adminService.findByLogin(adminLogin);
            AdminDTO adminDTO = new AdminDTO(admin.getClientID(), admin.getClientLogin(), admin.isClientStatusActive());
            return this.generateResponseEntityForDTO(adminDTO);
        } catch (AdminServiceAdminNotFoundException exception) {
            return ResponseEntity.notFound().build();
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<?> findAllWithMatchingLogin(@RequestParam("match") String adminLogin) {
        try {
            List<AdminDTO> listOfDTOs = this.getListOfAdminDTOs(this.adminService.findAllMatchingLogin(adminLogin));
            return this.generateResponseEntityForListOfDTOs(listOfDTOs);
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @GetMapping(value = "/{id}/ticket-list", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<?> getTicketsForCertainUser(@PathVariable("id") UUID adminID) {
        try {
            List<Ticket> listOfTicketsForAnAdmin = this.adminService.getTicketsForClient(adminID);
            List<TicketDTO> listOfDTOs = new ArrayList<>();
            for (Ticket ticket : listOfTicketsForAnAdmin) {
                listOfDTOs.add(new TicketDTO(ticket.getTicketID(), ticket.getMovieTime(), ticket.getTicketFinalPrice(), ticket.getClient().getClientID(), ticket.getMovie().getMovieID()));
            }
            if (listOfTicketsForAnAdmin.isEmpty()) {
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
            List<Admin> listOfAdmins = this.adminService.findAll();
            List<AdminDTO> listOfDTOs = this.getListOfAdminDTOs(listOfAdmins);
            return this.generateResponseEntityForListOfDTOs(listOfDTOs);
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@RequestBody AdminPasswordDTO adminPasswordDTO) {
        try {
            Admin admin = new Admin(adminPasswordDTO.getAdminID(), adminPasswordDTO.getAdminLogin(), adminPasswordDTO.getAdminPassword(), adminPasswordDTO.isAdminStatusActive());

            Set<ConstraintViolation<Admin>> violationSet = validator.validate(admin);
            List<String> messages = violationSet.stream().map(ConstraintViolation::getMessage).toList();
            if (!violationSet.isEmpty()) {
                return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(messages);
            }

            this.adminService.update(admin);
            return ResponseEntity.noContent().build();
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @PostMapping(value = "/{id}/activate")
    @Override
    public ResponseEntity<?> activate(@PathVariable("id") UUID adminID) {
        try {
            this.adminService.activate(adminID);
            return ResponseEntity.noContent().build();
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @PostMapping(value = "/{id}/deactivate")
    @Override
    public ResponseEntity<?> deactivate(@PathVariable("id") UUID adminID) {
        try {
            this.adminService.deactivate(adminID);
            return ResponseEntity.noContent().build();
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    private List<AdminDTO> getListOfAdminDTOs(List<Admin> listOfAdmins) {
        List<AdminDTO> listOfDTOs = new ArrayList<>();
        for (Admin admin : listOfAdmins) {
            listOfDTOs.add(new AdminDTO(admin.getClientID(), admin.getClientLogin(), admin.isClientStatusActive()));
        }
        return listOfDTOs;
    }

    private ResponseEntity<?> generateResponseEntityForDTO(AdminDTO adminDTO) {
        if (adminDTO == null) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(adminDTO);
        }
    }

    private ResponseEntity<?> generateResponseEntityForListOfDTOs(List<AdminDTO> listOfDTOs) {
        if (listOfDTOs.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(listOfDTOs);
        }
    }
}
