package pl.tks.gr3.cinema.adapters.exceptions.other.movie;

import pl.tks.gr3.cinema.adapters.exceptions.GeneralRepositoryException;

public class ResourceIsCurrentlyUsedDeleteException extends GeneralRepositoryException {
    public ResourceIsCurrentlyUsedDeleteException(String message) {
        super(message);
    }
}
