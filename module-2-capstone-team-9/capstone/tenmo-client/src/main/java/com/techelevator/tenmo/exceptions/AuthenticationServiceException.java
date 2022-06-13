package com.techelevator.tenmo.exceptions;

public class AuthenticationServiceException extends Exception{
    private static final long serialVersionUID = 1l;
    public AuthenticationServiceException(String errorMessage) {
        super(errorMessage);
    }
}
