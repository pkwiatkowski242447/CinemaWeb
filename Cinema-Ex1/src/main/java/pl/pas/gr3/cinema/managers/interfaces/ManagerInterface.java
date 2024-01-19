package pl.pas.gr3.cinema.managers.interfaces;

import pl.pas.gr3.cinema.exceptions.managers.GeneralManagerException;

import java.util.List;
import java.util.UUID;

public interface ManagerInterface<Type> {

    // Read methods

    Type findByUUID(UUID elementID) throws GeneralManagerException;
    List<Type> findAll() throws GeneralManagerException;
}
