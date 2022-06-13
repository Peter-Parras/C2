package com.techelevator.tenmo.exceptions;

public class UserNotFound extends Exception{
    public UserNotFound() {
        super ("User not found, try a new user Id");
    }
}
