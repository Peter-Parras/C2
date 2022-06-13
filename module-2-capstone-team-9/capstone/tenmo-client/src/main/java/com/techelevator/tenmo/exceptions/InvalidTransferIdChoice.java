package com.techelevator.tenmo.exceptions;

public class InvalidTransferIdChoice extends Exception{
    public InvalidTransferIdChoice() {
        super("Invalid transfer Id, please try again");
    }
}
