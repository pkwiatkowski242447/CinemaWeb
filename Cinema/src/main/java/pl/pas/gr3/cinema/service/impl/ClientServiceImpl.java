package pl.pas.gr3.cinema.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.pas.gr3.cinema.util.consts.model.UserConstants;
import pl.pas.gr3.cinema.repository.impl.AccountRepositoryImpl;
import pl.pas.gr3.cinema.service.api.AccountService;
import pl.pas.gr3.cinema.entity.Ticket;
import pl.pas.gr3.cinema.entity.account.Client;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements AccountService<Client> {

    private final AccountRepositoryImpl accountRepository;

    @Override
    public Client create(String login, String password) {
        return accountRepository.createClient(login, password);
    }

    @Override
    public Client findByUUID(UUID accountId) {
        return accountRepository.findClientByUUID(accountId);
    }

    @Override
    public Client findByLogin(String login) {
        return accountRepository.findClientByLogin(login);
    }

    @Override
    public List<Client> findAllMatchingLogin(String loginToBeMatched) {
        return accountRepository.findAllClientsMatchingLogin(loginToBeMatched);
    }

    @Override
    public List<Client> findAll() {
        return accountRepository.findAllClients();
    }

    @Override
    public void update(Client client) {
        accountRepository.updateClient(client);
    }

    @Override
    public void activate(UUID accountId) {
        Client foundClient = accountRepository.findClientByUUID(accountId);
        accountRepository.activate(foundClient, UserConstants.CLIENT_DISCRIMINATOR);
    }

    @Override
    public void deactivate(UUID accountId) {
        Client foundClient = accountRepository.findClientByUUID(accountId);
        accountRepository.deactivate(foundClient, UserConstants.CLIENT_DISCRIMINATOR);
    }

    @Override
    public List<Ticket> getTicketsForClient(UUID accountId) {
        return accountRepository.getListOfTicketsForClient(accountId, UserConstants.CLIENT_DISCRIMINATOR);
    }

    @Override
    public void delete(UUID accountId) {
        accountRepository.delete(accountId, UserConstants.CLIENT_DISCRIMINATOR);
    }
}
