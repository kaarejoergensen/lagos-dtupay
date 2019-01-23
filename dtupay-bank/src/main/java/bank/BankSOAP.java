package bank;

import bank.dtu.ws.fastmoney.exceptions.BankServiceException_Exception;
import bank.dtu.ws.fastmoney.service.BankService;
import bank.dtu.ws.fastmoney.service.BankServiceService;
import models.Account;
import models.AccountInfo;
import models.User;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
/**
 * @author Asge
 */
public class BankSOAP implements Bank {
    private BankService bankService;

    public BankSOAP() {
        this.bankService = new BankServiceService().getBankServicePort();
    }

    @Override
    public String createAccountWithBalance(User user, BigDecimal balance) throws BankServiceException_Exception {
        return bankService.createAccountWithBalance(user, balance);
    }

    @Override
    public Account getAccount(String id) throws BankServiceException_Exception {
        return bankService.getAccount(id);
    }

    @Override
    public Account getAccountByCprNumber(String cprNumber) throws BankServiceException_Exception {
        return bankService.getAccountByCprNumber(cprNumber);
    }

    @Override
    public List<AccountInfo> getAccounts() {
        List<AccountInfo> accountInfos = bankService.getAccounts();
        return accountInfos != null ? accountInfos : new ArrayList<>();
    }

    @Override
    public void retireAccount(String id) throws BankServiceException_Exception {
        bankService.retireAccount(id);
    }

    @Override
    public void transferMoneyFromTo(String fromAccountId, String toAccountId, BigDecimal amount, String description) throws BankServiceException_Exception {
        bankService.transferMoneyFromTo(fromAccountId, toAccountId, amount, description);
    }

    @Override
    public Boolean accountExists(String cpr){
        Boolean result = false;
        try {
            Account tempAccount = getAccountByCprNumber(cpr);
            result = true;
        } finally {
            return result;
        }


    }


}
