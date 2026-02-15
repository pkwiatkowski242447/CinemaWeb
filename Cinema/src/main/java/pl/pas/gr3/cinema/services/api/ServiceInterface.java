package pl.pas.gr3.cinema.services.api;

import pl.pas.gr3.cinema.exception.services.GeneralServiceException;

import java.util.List;
import java.util.UUID;

public interface ServiceInterface<Type> {

    // Read methods

    Type findByUUID(UUID elementID) throws GeneralServiceException;
    List<Type> findAll() throws GeneralServiceException;
}
