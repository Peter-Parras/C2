package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.TransferType;
public interface TransferTypeDAO {
    TransferType getTransferTypeFromDescription(String description);
    TransferType getTransferTypeFromId(int transferId);

}



