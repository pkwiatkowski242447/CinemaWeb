package pl.pas.gr3.cinema.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.pas.gr3.cinema.entity.account.Account;
import pl.pas.gr3.cinema.mapper.AccountMapper;
import pl.pas.gr3.cinema.repository.api.AccountRepository;
import pl.pas.gr3.cinema.util.consts.model.UserConstants;
import pl.pas.gr3.cinema.service.api.AccountService;
import pl.pas.gr3.cinema.entity.Ticket;
import pl.pas.gr3.cinema.entity.account.Admin;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AccountService<Admin> {

    private final AccountRepository accountRepository;

    private final AccountMapper accountMapper;

    @Override
    public Admin create(String login, String password) {
        return accountRepository.createAdmin(login, password);
    }

    @Override
    public Admin findByUUID(UUID accountId) {
        Account foundAccount = accountRepository.findByUUID(accountId);
        return accountMapper.toAdmin(foundAccount);
    }

    @Override
    public Admin findByLogin(String login) {
        return accountRepository.findAdminByLogin(login);
    }

    @Override
    public List<Admin> findAllMatchingLogin(String loginToBeMatched) {
        return accountRepository.findAllAdminsMatchingLogin(loginToBeMatched);
    }

    @Override
    public List<Admin> findAll() {
        return accountRepository.findAllAdmins();
    }

    @Override
    public void update(Admin admin) {
        accountRepository.updateAdmin(admin);
    }

    @Override
    public void activate(UUID accountId) {
        Account foundAdminAccount = accountRepository.findByUUID(accountId);
        Admin foundAdmin = accountMapper.toAdmin(foundAdminAccount);
        accountRepository.activate(foundAdmin, UserConstants.ADMIN_DISCRIMINATOR);
    }

    @Override
    public void deactivate(UUID accountId) {
        Account foundAdminAccount = accountRepository.findByUUID(accountId);
        Admin foundAdmin = accountMapper.toAdmin(foundAdminAccount);
        accountRepository.deactivate(foundAdmin, UserConstants.ADMIN_DISCRIMINATOR);
    }

    @Override
    public List<Ticket> getTicketsForClient(UUID accountId) {
        return accountRepository.getListOfTicketsForClient(accountId, UserConstants.ADMIN_DISCRIMINATOR);
    }

    @Override
    public void delete(UUID accountId) {
        accountRepository.delete(accountId, UserConstants.ADMIN_DISCRIMINATOR);
    }
}
