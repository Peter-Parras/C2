package com.techelevator.tenmo.exceptions;

public class InvalidUserChoice extends Exception {
    public InvalidUserChoice() {
        super ("Sorry, cannot pay yourself;)");
    }

}
