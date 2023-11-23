package pl.pas.gr3.cinema.managers;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/hello-world")
public class TestManager {
    @GET
    @Produces("text/plain")
    public String hello() {
        return "Hello, World!";
    }
}
