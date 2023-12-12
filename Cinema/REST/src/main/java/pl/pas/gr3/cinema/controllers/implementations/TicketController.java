package pl.pas.gr3.cinema.controllers.implementations;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pas.gr3.cinema.exceptions.services.crud.ticket.TicketServiceTicketNotFoundException;
import pl.pas.gr3.dto.TicketDTO;
import pl.pas.gr3.dto.TicketInputDTO;
import pl.pas.gr3.cinema.exceptions.services.GeneralServiceException;
import pl.pas.gr3.cinema.services.implementations.TicketService;
import pl.pas.gr3.cinema.model.Ticket;
import pl.pas.gr3.cinema.controllers.interfaces.TicketServiceInterface;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tickets")
public class TicketController implements TicketServiceInterface {

    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private final TicketService ticketService;

    @Autowired
    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<?> create(@RequestBody TicketInputDTO ticketInputDTO) {
        try {
            Ticket ticket = this.ticketService.create(ticketInputDTO.getMovieTime(), ticketInputDTO.getClientID(), ticketInputDTO.getMovieID(), ticketInputDTO.getTicketType());

            Set<ConstraintViolation<Ticket>> violationSet = validator.validate(ticket);
            List<String> messages = violationSet.stream().map(ConstraintViolation::getMessage).toList();
            if (!violationSet.isEmpty()) {
                return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(messages);
            }

            TicketDTO ticketDTO = new TicketDTO(ticket.getTicketID(), ticket.getMovieTime(), ticket.getTicketFinalPrice(), ticket.getClient().getClientID(), ticket.getMovie().getMovieID());
            return ResponseEntity.created(URI.create("/" + ticketDTO.getTicketID().toString())).contentType(MediaType.APPLICATION_JSON).body(ticketDTO);
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<?> findByUUID(@PathVariable("id") UUID ticketID) {
        try {
            Ticket ticket = this.ticketService.findByUUID(ticketID);
            TicketDTO ticketDTO = new TicketDTO(ticket.getTicketID(), ticket.getMovieTime(), ticket.getTicketFinalPrice(), ticket.getClient().getClientID(), ticket.getMovie().getMovieID());
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(ticketDTO);
        } catch (TicketServiceTicketNotFoundException exception) {
            return ResponseEntity.notFound().build();
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<?> findAll() {
        try {
            List<Ticket> listOfFoundTickets = this.ticketService.findAll();
            List<TicketDTO> listOfDTOs = new ArrayList<>();
            for (Ticket ticket : listOfFoundTickets) {
                listOfDTOs.add(new TicketDTO(ticket.getTicketID(), ticket.getMovieTime(), ticket.getTicketFinalPrice(), ticket.getClient().getClientID(), ticket.getMovie().getMovieID()));
            }

            if (listOfDTOs.isEmpty()) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(listOfDTOs);
            }
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<?> update(@RequestBody TicketDTO ticketDTO) {
        try {
            Ticket ticket = this.ticketService.findByUUID(ticketDTO.getTicketID());
            ticket.setMovieTime(ticketDTO.getMovieTime());

            Set<ConstraintViolation<Ticket>> violationSet = validator.validate(ticket);
            List<String> messages = violationSet.stream().map(ConstraintViolation::getMessage).toList();
            if (!violationSet.isEmpty()) {
                return ResponseEntity.badRequest().body(messages);
            }

            this.ticketService.update(ticket);
            return ResponseEntity.noContent().build();
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }

    @DeleteMapping(value = "/{id}")
    @Override
    public ResponseEntity<?> delete(@PathVariable("id") UUID ticketID) {
        try {
            this.ticketService.delete(ticketID);
            return ResponseEntity.noContent().build();
        } catch (GeneralServiceException exception) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(exception.getMessage());
        }
    }
}
