package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
public interface TransferService {
    void createTransfer(AuthenticatedUser authenticatedUser, Transfer transfer);
    Transfer[] getTransfersFromUserId(AuthenticatedUser authenticatedUser, long userId);
    Transfer getTransferFromTransferId(AuthenticatedUser authenticatedUser, int id);
    Transfer[] getAllTransfers(AuthenticatedUser authenticatedUser);
    Transfer[] getPendingTransfersByUserId(AuthenticatedUser authenticatedUser);
    void updateTransfer(AuthenticatedUser authenticatedUser, Transfer transfer);
}



