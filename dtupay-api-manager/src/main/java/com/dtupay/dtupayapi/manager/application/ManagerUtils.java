package com.dtupay.dtupayapi.manager.application;

import clients.BankClient;
import clients.TokenClient;
import exceptions.ClientException;
import models.Account;
import models.AccountInfo;
import models.Transaction;
import models.User;

import javax.ws.rs.client.Client;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 @author Fredrik
 */

public class ManagerUtils {

    private TokenClient tokenClient;
    private BankClient bankClient;

    public ManagerUtils(TokenClient tokenClient, BankClient bankclient){
        this.tokenClient = tokenClient;
        this.bankClient = bankclient;
    }



    public boolean createAccount(String cpr, String firstName, String lastName, BigDecimal initialBalance) throws ClientException {
        if(bankClient.getAccount(cpr) != null) return false;
            bankClient.createAccountWithBalance(new User(cpr,firstName,lastName),initialBalance);
            return true;
    }


}
