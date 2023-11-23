package pl.pas.gr3.cinema.managers;


import jakarta.ws.rs.core.Response;

import java.util.UUID;

public abstract class Manager<Type> {

    // Read methods from CRUD - findByUUID, findAll, findAllUUIDs

    public abstract Response findByUUID(UUID elementID);
    public abstract Response findAll();

    // Update methods from CRUD - update

    public abstract Response update(Type element);
}
