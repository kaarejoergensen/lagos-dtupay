package com.dtupay.dtupayapi.manager.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import models.Transaction;
import models.User;

import java.util.List;

@Data
@AllArgsConstructor
public class UserModel {
    private User user;
    private List<Transaction> transactions;
}
