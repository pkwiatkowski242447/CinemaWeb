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
import pl.pas.gr3.cinema.dto.MovieDTO;
import pl.pas.gr3.cinema.dto.TicketDTO;
import pl.pas.gr3.cinema.exceptions.managers.GeneralManagerException;
import pl.pas.gr3.cinema.managers.implementations.MovieManager;
import pl.pas.gr3.cinema.model.Movie;
import pl.pas.gr3.cinema.model.Ticket;
import pl.pas.gr3.cinema.services.interfaces.MovieServiceInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@ApplicationScoped
@Path("/movies")
@Named
public class MovieService implements MovieServiceInterface {

    private final static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Inject
    private MovieManager movieManager;

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response create(@QueryParam("title") String movieTitle, @QueryParam("price") double movieBasePrice, @QueryParam("scr-room") int screeningRoomNumber, @QueryParam("number") int numberOfAvailableSeats) {
        try {
            Movie movie = this.movieManager.create(movieTitle, movieBasePrice, screeningRoomNumber, numberOfAvailableSeats);
            Set<ConstraintViolation<Movie>> violationSet = validator.validate(movie);
            List<String> messages = violationSet.stream().map(ConstraintViolation::getMessage).toList();
            if (!violationSet.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).entity(messages).build();
            }
            MovieDTO movieDTO = new MovieDTO(movie.getMovieID(), movie.getMovieTitle(), movie.getMovieBasePrice(), movie.getScrRoomNumber(), movie.getNumberOfAvailableSeats());
            return Response.status(Response.Status.CREATED).entity(movieDTO).build();
        } catch (GeneralManagerException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response findByUUID(@PathParam("id") UUID movieID) {
        try {
            Movie movie = this.movieManager.findByUUID(movieID);
            MovieDTO movieDTO = new MovieDTO(movie.getMovieID(), movie.getMovieTitle(), movie.getMovieBasePrice(), movie.getScrRoomNumber(), movie.getNumberOfAvailableSeats());
            if (movie == null) {
                return Response.status(Response.Status.NO_CONTENT).build();
            } else {
                return Response.status(Response.Status.OK).entity(movieDTO).build();
            }
        } catch (GeneralManagerException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response findAll() {
        try {
            List<Movie> listOfFoundMovies = this.movieManager.findAll();
            List<MovieDTO> listOfDTOs = new ArrayList<>();
            for (Movie movie : listOfFoundMovies) {
                listOfDTOs.add(new MovieDTO(movie.getMovieID(), movie.getMovieTitle(), movie.getMovieBasePrice(), movie.getScrRoomNumber(), movie.getNumberOfAvailableSeats()));
            }
            if (listOfFoundMovies.isEmpty()) {
                return Response.status(Response.Status.NO_CONTENT).build();
            } else {
                return Response.status(Response.Status.OK).entity(listOfDTOs).build();
            }
        } catch (GeneralManagerException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }
    }

    @GET
    @Path("/tickets")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response findAllTicketsForCertainMovie(UUID movieID) {
        List<Ticket> listOfTickets = this.movieManager.getListOfTicketsForCertainMovie(movieID);
        List<TicketDTO> listOfDTOs = new ArrayList<>();
        for (Ticket ticket : listOfTickets) {
            listOfDTOs.add(new TicketDTO(ticket.getTicketID(), ticket.getMovieTime(), ticket.getTicketFinalPrice(), ticket.getClient().getClientID(), ticket.getMovie().getMovieID()));
        }
        if (listOfTickets.isEmpty()) {
            return Response.status(Response.Status.NO_CONTENT).type(MediaType.APPLICATION_JSON).build();
        } else {
            return Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON).entity(listOfDTOs).build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Override
    public Response update(Movie movie) {
        try {
            Set<ConstraintViolation<Movie>> violationSet = validator.validate(movie);
            List<String> messages = violationSet.stream().map(ConstraintViolation::getMessage).toList();
            if (!violationSet.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).entity(messages).build();
            }
            this.movieManager.update(movie);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (GeneralManagerException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Override
    public Response delete(@PathParam("id") UUID movieID) {
        try {
            this.movieManager.delete(movieID);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (GeneralManagerException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }
    }
}
