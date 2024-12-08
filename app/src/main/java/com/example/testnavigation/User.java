package com.example.testnavigation;

public class User {

    private String emailAddress;
    private String firstName;
    private String lastName;
    private String password;


    public User(String emailAddress, String firstName, String lastName, String password){
        this.emailAddress = emailAddress;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
    }

    public String getEmailAddress(){
        return this.emailAddress;
    }

    public String getFirstName(){
        return this.firstName;
    }

    public String getLastName(){
        return this.lastName;
    }

    public String getPassword(){
        return this.password;
    }

    public void setEmailAddress(String emailAddress){
        this.emailAddress = emailAddress;
    }
    public void setFirstName (String firstName){
        this.firstName = firstName;
    }
    public void setLastName (String lastName){
        this.lastName = lastName;
    }
    public void setPassword (String password){
        this.password = password;
    }
}
