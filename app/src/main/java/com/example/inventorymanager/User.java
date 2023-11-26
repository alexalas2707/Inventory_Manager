package com.example.inventorymanager;

import org.mindrot.jbcrypt.BCrypt;

public class User {
    private String name;
    private String lastname;
    private String username;
    private String password; // This should be a hashed password

    public User(String name, String lastname, String username, String password) {
        this.name = name;
        this.lastname = lastname;
        this.username = username;
        this.password = password;
    }

    // Getters and setters for each field
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    // Important: Password should be hashed
    public void setPassword(String password) {
        this.password = hashPassword(password);
    }


    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}

