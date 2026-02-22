package pl.pas.gr3.cinema.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.pas.gr3.cinema.entity.account.Account;
import pl.pas.gr3.cinema.mapper.AccountMapper;
import pl.pas.gr3.cinema.repository.api.AccountRepository;
import pl.pas.gr3.cinema.util.consts.model.UserConstants;
import pl.pas.gr3.cinema.service.api.AccountService;
import pl.pas.gr3.cinema.entity.Ticket;
import pl.pas.gr3.cinema.entity.account.Staff;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StaffServiceImpl implements AccountService<Staff> {

    private final AccountRepository accountRepository;

    private final AccountMapper accountMapper;

    @Override
    public Staff create(String login, String password) {
        return accountRepository.createStaff(login, password);
    }

    @Override
    public Staff findByUUID(UUID accountId) {
        Account foundStaffAccount = accountRepository.findByUUID(accountId);
        return accountMapper.toStaff(foundStaffAccount);
    }

    @Override
    public Staff findByLogin(String login) {
        return accountRepository.findStaffByLogin(login);
    }

    @Override
    public List<Staff> findAllMatchingLogin(String loginToBeMatched) {
        return accountRepository.findAllStaffsMatchingLogin(loginToBeMatched);
    }

    @Override
    public List<Staff> findAll() {
        return accountRepository.findAllStaffs();
    }

    @Override
    public void update(Staff staff) {
        accountRepository.updateStaff(staff);
    }

    @Override
    public void activate(UUID accountId) {
        Account foundStaffAccount = accountRepository.findByUUID(accountId);
        Staff foundStaff = accountMapper.toStaff(foundStaffAccount);
        accountRepository.activate(foundStaff, UserConstants.STAFF_DISCRIMINATOR);
    }

    @Override
    public void deactivate(UUID accountId) {
        Account foundStaffAccount = accountRepository.findByUUID(accountId);
        Staff foundStaff = accountMapper.toStaff(foundStaffAccount);
        accountRepository.deactivate(foundStaff, UserConstants.STAFF_DISCRIMINATOR);
    }

    @Override
    public List<Ticket> getTicketsForClient(UUID accountId) {
        return accountRepository.getListOfTicketsForClient(accountId, UserConstants.STAFF_DISCRIMINATOR);
    }

    @Override
    public void delete(UUID accountId) {
        accountRepository.delete(accountId, UserConstants.STAFF_DISCRIMINATOR);
    }
}
