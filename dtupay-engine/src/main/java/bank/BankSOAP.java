package bank;

import bank.dtu.ws.fastmoney.exceptions.BankServiceException_Exception;
import bank.dtu.ws.fastmoney.service.BankService;
import bank.dtu.ws.fastmoney.service.BankServiceService;
import models.Account;
import models.AccountInfo;
import models.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class BankSOAP implements Bank {
    private BankService bankService;

    public BankSOAP() {
        this.bankService = new BankServiceService().getBankServicePort();
    }

    @Override
    public Optional<String> createAccountWithBalance(User user, BigDecimal balance) {
        try {
            return Optional.of(bankService.createAccountWithBalance(user, balance));
        } catch (BankServiceException_Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Account> getAccount(String id) {
        try {
            return Optional.of(bankService.getAccount(id));
        } catch (BankServiceException_Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Account> getAccountByCprNumber(String cprNumber) {
        try {
            return Optional.of(bankService.getAccountByCprNumber(cprNumber));
        } catch (BankServiceException_Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<List<AccountInfo>> getAccounts() {
        List<AccountInfo> accountInfos = bankService.getAccounts();
        return accountInfos != null ? Optional.of(accountInfos) : Optional.empty();
    }

    @Override
    public boolean retireAccount(String id) {
        try {
            bankService.retireAccount(id);
            return true;
        } catch (BankServiceException_Exception e) {
            return false;
        }
    }

    @Override
    public boolean transferMoneyFromTo(String arg0, String arg1, BigDecimal arg2, String arg3) {
        try {
            bankService.transferMoneyFromTo(arg0, arg1, arg2, arg3);
            return true;
        } catch (BankServiceException_Exception e) {
            return false;
        }
    }
}
