package pl.pas.gr3.cinema.repositories.api;

import pl.pas.gr3.cinema.exception.repositories.GeneralRepositoryException;

import java.util.UUID;

public interface RepositoryInterface<Type> {

    // CRUD methods

    // Read methods

    Type findByUUID(UUID elementID) throws GeneralRepositoryException;
}
