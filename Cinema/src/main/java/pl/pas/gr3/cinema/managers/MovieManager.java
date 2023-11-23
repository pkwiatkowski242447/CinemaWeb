package pl.pas.gr3.cinema.managers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pl.pas.gr3.cinema.exceptions.repositories.MovieRepositoryException;
import pl.pas.gr3.cinema.model.Movie;
import pl.pas.gr3.cinema.repositories.implementations.MovieRepository;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
@Path("/movies")
@Named
public class MovieManager extends Manager<Movie> {

    @Inject
    private MovieRepository movieRepository;

    @POST
    @Path("/create")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@QueryParam("title") String movieTitle, @QueryParam("price") double movieBasePrice, @QueryParam("scr-room") int screeningRoomNumber, @QueryParam("number") int numberOfAvailableSeats) {
        try {
            Movie movie = this.movieRepository.create(movieTitle, movieBasePrice, screeningRoomNumber, numberOfAvailableSeats);
            return Response.status(Response.Status.CREATED).entity(movie).build();
        } catch (MovieRepositoryException exception) {
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
            Movie movie = this.movieRepository.findByUUID(movieID);
            if (movie == null) {
                return Response.status(Response.Status.NO_CONTENT).build();
            } else {
                return Response.status(Response.Status.OK).entity(movie).build();
            }
        } catch (MovieRepositoryException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response findAll() {
        try {
            List<Movie> listOfFoundMovies = this.movieRepository.findAll();
            if (listOfFoundMovies.isEmpty()) {
                return Response.status(Response.Status.NO_CONTENT).build();
            } else {
                return Response.status(Response.Status.OK).entity(listOfFoundMovies).build();
            }
        } catch (MovieRepositoryException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }
    }

    @PUT
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Override
    public Response update(Movie movie) {
        try {
            this.movieRepository.update(movie);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (MovieRepositoryException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }
    }

    @DELETE
    @Path("/delete/{id}")
    public Response delete(@PathParam("id") UUID movieID) {
        try {
            this.movieRepository.delete(movieID);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (MovieRepositoryException exception) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }
    }
}
