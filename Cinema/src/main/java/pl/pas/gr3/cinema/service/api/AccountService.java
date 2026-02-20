package pl.pas.gr3.cinema.service.api;

import pl.pas.gr3.cinema.entity.Ticket;

import java.util.List;
import java.util.UUID;

public interface AccountService<T> {

    /* CREATE */

    T create(String login, String password);

    /* READ */

    T findByUUID(UUID accountId);
    List<T> findAll();

    T findByLogin(String login);
    List<T> findAllMatchingLogin(String loginToBeMatched);
    List<Ticket> getTicketsForClient(UUID accountId);

    /* UPDATE */

    void update(T element);

    /* DELETE */

    void delete(UUID accountId);

    /* OTHER */

    void activate(UUID accountId);
    void deactivate(UUID accountId);
}
