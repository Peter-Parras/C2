package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.TransferStatus;

public interface TransferStatusService {
    TransferStatus getTransferStatus(AuthenticatedUser authenticatedUser, String description);
    TransferStatus getTransferStatusById(AuthenticatedUser authenticatedUser, int transferStatusId);
}


