package pl.pas.gr3.cinema.repositories.interfaces;

import java.util.List;
import java.util.UUID;

public interface RepositoryInterface<Type> {

    // CRUD methods

    // Read methods

    Type findByUUID(UUID elementID);
    List<Type> findAll();
    List<UUID> findAllUUIDs();

    // Update methods

    void update(Type element);

    // Delete methods

    void delete(UUID elementID);
}
