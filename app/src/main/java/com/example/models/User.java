package com.example.models;

public class User {

    private int userID;
    private String username;
    private String fullName;
    private String email;
    private String password;
    private String type;

    public User() {
    }

    public User(
            int userID,
            String username,
            String fullName,
            String email,
            String password,
            String type
    ) {
        this.userID = userID;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.type = type;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}