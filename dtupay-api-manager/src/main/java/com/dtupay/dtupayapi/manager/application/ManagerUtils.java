package com.dtupay.dtupayapi.manager.application;

import clients.BankClient;
import clients.TokenClient;
import com.dtupay.dtupayapi.manager.Models.UserModel;
import exceptions.ClientException;
import models.AccountInfo;
import models.Transaction;
import models.User;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ManagerUtils {

    private TokenClient tokenClient;
    private BankClient bankClient;

    public ManagerUtils(TokenClient tokenClient, BankClient bankclient){
        this.tokenClient = tokenClient;
        this.bankClient = bankclient;
    }




    public boolean createAccount(String cpr, String firstName, String lastName, BigDecimal initialBalance) throws ClientException {
         /*
        Check if user already have an account
        */
        if(bankClient.getAccount(cpr) != null) return false;
            bankClient.createAccountWithBalance(new User(cpr,firstName,lastName),initialBalance);
            return true;
    }

    public UserModel getUser(String userID) throws ClientException {
        try {
            User user = bankClient.getAccount(userID).getUser();
            List<Transaction> trans = bankClient.getAccount(userID).getTransactions();
            return new UserModel(user,trans);
        } catch (ClientException e) {
            throw new ClientException();
        }
    }

    public List<UserModel> getAllUsers() throws ClientException {
        try {
            List<UserModel> userModels = new ArrayList<>();
            for(AccountInfo acc : bankClient.getAccounts()){
                List<Transaction> userTrans = bankClient.getAccount(acc.getUser().getCprNumber()).getTransactions();
                userModels.add(new UserModel(acc.getUser(),userTrans));
            }
            return userModels;
        } catch (ClientException e) {
            throw new ClientException();
        }
    }
}
