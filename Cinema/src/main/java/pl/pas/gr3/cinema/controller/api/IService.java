package pl.pas.gr3.cinema.controller.api;

import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface IService<Type> {

    // CRUD methods

    // Read methods

    ResponseEntity<?> findByUUID(UUID elementID);
    ResponseEntity<?> findAll();
}
