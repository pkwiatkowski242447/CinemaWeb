package pl.pas.gr3.cinema.managers.implementations;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import pl.pas.gr3.cinema.exceptions.managers.crud.ticket.TicketManagerCreateException;
import pl.pas.gr3.cinema.exceptions.managers.crud.ticket.TicketManagerDeleteException;
import pl.pas.gr3.cinema.exceptions.managers.crud.ticket.TicketManagerReadException;
import pl.pas.gr3.cinema.exceptions.managers.crud.ticket.TicketManagerUpdateException;
import pl.pas.gr3.cinema.exceptions.repositories.TicketRepositoryException;
import pl.pas.gr3.cinema.managers.interfaces.TicketManagerInterface;
import pl.pas.gr3.cinema.model.Ticket;
import pl.pas.gr3.cinema.model.TicketType;
import pl.pas.gr3.cinema.repositories.implementations.TicketRepository;

import java.io.Closeable;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class TicketManager implements TicketManagerInterface, Closeable {

    @Inject
    private TicketRepository ticketRepository;

    public TicketManager() {
    }

    public TicketManager(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Override
    public Ticket create(String movieTime, UUID clientID, UUID movieID, String ticketType) throws TicketManagerCreateException {
        try {
            LocalDateTime movieTimeParsed = LocalDateTime.parse(movieTime);
            if (ticketType.equals("reduced")) {
                return this.ticketRepository.create(movieTimeParsed, clientID, movieID, TicketType.REDUCED);
            } else {
                return this.ticketRepository.create(movieTimeParsed, clientID, movieID, TicketType.NORMAL);
            }
        } catch (TicketRepositoryException | DateTimeParseException exception) {
            throw new TicketManagerCreateException(exception.getMessage(), exception);
        }
    }

    @Override
    public Ticket findByUUID(UUID ticketID) throws TicketManagerReadException {
        try {
            return this.ticketRepository.findByUUID(ticketID);
        } catch (TicketRepositoryException exception) {
            throw new TicketManagerReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public List<Ticket> findAll() throws TicketManagerReadException {
        try {
            return this.ticketRepository.findAll();
        } catch (TicketRepositoryException exception) {
            throw new TicketManagerReadException(exception.getMessage(), exception);
        }
    }

    @Override
    public void update(Ticket ticket) throws TicketManagerUpdateException {
        try {
            this.ticketRepository.update(ticket);
        } catch (TicketRepositoryException exception) {
            throw new TicketManagerUpdateException(exception.getMessage(), exception);
        }
    }

    @Override
    public void delete(UUID ticketID) throws TicketManagerDeleteException {
        try {
            this.ticketRepository.delete(ticketID);
        } catch (TicketRepositoryException exception) {
            throw new TicketManagerDeleteException(exception.getMessage(), exception);
        }
    }

    @Override
    public void close() {
        this.ticketRepository.close();
    }
}
