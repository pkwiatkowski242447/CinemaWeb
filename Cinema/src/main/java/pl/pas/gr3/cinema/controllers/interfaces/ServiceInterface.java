package pl.pas.gr3.cinema.controllers.interfaces;

import jakarta.ws.rs.core.Response;

import java.util.UUID;

public interface ServiceInterface<Type> {

    // CRUD methods

    // Read methods

    Response findByUUID(UUID elementID);
    Response findAll();
}
