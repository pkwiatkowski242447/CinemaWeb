package pl.pas.gr3.cinema.exceptions.repositories;

public class ResourceIsCurrentlyUsedDeleteException extends GeneralRepositoryException {
    public ResourceIsCurrentlyUsedDeleteException(String message) {
        super(message);
    }
}
