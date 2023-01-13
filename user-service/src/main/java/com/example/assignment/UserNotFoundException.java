package com.example.assignment;

public class UserNotFoundException extends Exception{

    public UserNotFoundException()
    {
        super("user not found");
    }

}
