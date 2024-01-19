package pl.pas.gr3.cinema.exceptions.repositories.other.movie;

import pl.pas.gr3.cinema.exceptions.repositories.GeneralRepositoryException;

public class ResourceIsCurrentlyUsedDeleteException extends GeneralRepositoryException {
    public ResourceIsCurrentlyUsedDeleteException(String message) {
        super(message);
    }
}
