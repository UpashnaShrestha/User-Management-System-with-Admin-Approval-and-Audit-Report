package com.project.UserManagementSystem.exceptions;

public class AccountNotApprovedException extends RuntimeException{
    public AccountNotApprovedException(String message){
        super(message);
    }
}
