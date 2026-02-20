package pl.pas.gr3.cinema.repository.api;

import java.util.UUID;

public interface Repository<T> {

    /* READ */

    T findByUUID(UUID elementId);
}
