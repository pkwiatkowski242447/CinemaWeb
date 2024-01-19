package pl.pas.gr3.mvc.controller.ticket;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.pas.gr3.dto.MovieDTO;
import pl.pas.gr3.dto.TicketInputDTO;
import pl.pas.gr3.dto.users.ClientDTO;
import pl.pas.gr3.mvc.dao.implementations.ClientDao;
import pl.pas.gr3.mvc.dao.implementations.MovieDao;
import pl.pas.gr3.mvc.dao.interfaces.IClientDao;
import pl.pas.gr3.mvc.dao.interfaces.IMovieDao;
import pl.pas.gr3.mvc.exceptions.beans.tickets.TicketCreateException;
import pl.pas.gr3.mvc.exceptions.daos.client.ClientDaoReadException;
import pl.pas.gr3.mvc.exceptions.daos.movie.MovieDaoReadException;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@ViewScoped
@Named
public class CreateTicketBean implements Serializable {

    private ClientDTO clientDTO;
    private MovieDTO movieDTO;
    private LocalDateTime movieDateTime;

    private String message;

    private List<ClientDTO> listOfClientDTOs;
    private List<MovieDTO> listOfMovieDTOs;

    private IClientDao clientDao;
    private IMovieDao movieDao;

    @Inject
    private TicketControllerBean ticketControllerBean;

    @PostConstruct
    public void beanInit() {
        clientDao = new ClientDao();
        movieDao = new MovieDao();
        this.findAllClients();
        this.findAllMovies();
        clientDTO = ticketControllerBean.getSelectedClientDTO();
        movieDTO = ticketControllerBean.getSelectedMovieDTO();
    }

    public String createTicket() {
        try {
            ticketControllerBean.createTicket(new TicketInputDTO(movieDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), clientDTO.getClientID(), movieDTO.getMovieID()));
            return "ticketCreatedSuccessfully";
        } catch (TicketCreateException exception) {
            message = exception.getMessage();
            return "ticketCouldNotBeCreated";
        }
    }

    public void findAllClients() {
        try {
            listOfClientDTOs = clientDao.findAll();
        } catch (ClientDaoReadException exception) {
            message = exception.getMessage();
        }
    }

    public void findAllMovies() {
        try {
            listOfMovieDTOs = movieDao.findAll();
        } catch (MovieDaoReadException exception) {
            message = exception.getMessage();
        }
    }

    public String selectClient(ClientDTO clientDTO) {
        ticketControllerBean.setSelectedClientDTO(clientDTO);
        return "goToMovieSelection";
    }

    public String selectMovie(MovieDTO movieDTO) {
        ticketControllerBean.setSelectedMovieDTO(movieDTO);
        return "goToTicketDetails";
    }
}
