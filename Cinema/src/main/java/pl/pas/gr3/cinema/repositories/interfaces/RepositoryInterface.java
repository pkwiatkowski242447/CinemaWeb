package pl.pas.gr3.cinema.repositories.interfaces;

import pl.pas.gr3.cinema.exceptions.repositories.GeneralRepositoryException;

import java.util.List;
import java.util.UUID;

public interface RepositoryInterface<Type> {

    // CRUD methods

    // Read methods

    Type findByUUID(UUID elementID) throws GeneralRepositoryException;
    List<UUID> findAllUUIDs() throws GeneralRepositoryException;

    // Delete methods

    void delete(UUID elementID) throws GeneralRepositoryException;
}
