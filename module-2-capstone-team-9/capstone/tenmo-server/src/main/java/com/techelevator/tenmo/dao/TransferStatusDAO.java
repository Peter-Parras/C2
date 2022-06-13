package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.TransferStatus;
public interface TransferStatusDAO {
    TransferStatus getTransferStatusByDesc(String description);

    TransferStatus getTransferStatusById(int transferStatusId);
}



