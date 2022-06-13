package com.techelevator.tenmo.services;


import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Balance;

public interface AccountService {
    Balance getBalance(AuthenticatedUser authenticatedUser);
    Account getAccountByUserId(AuthenticatedUser authenticatedUser, int userId);
    Account getAccountById(AuthenticatedUser authenticatedUser, int accountId);







}


