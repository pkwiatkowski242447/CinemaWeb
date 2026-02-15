package pl.pas.gr3.cinema.exception.repositories.other.movie;

import pl.pas.gr3.cinema.exception.repositories.GeneralRepositoryException;

public class ResourceIsCurrentlyUsedDeleteException extends GeneralRepositoryException {
    public ResourceIsCurrentlyUsedDeleteException(String message) {
        super(message);
    }
}
