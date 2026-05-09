package com.project.expensetrackerapi.domain;

import java.io.Serializable;

public class User implements Serializable {

    private static final long serialVersionUID = 3L;

    private Integer userId;
    private String  firstName;
    private String  lastName;
    private String  email;
    private String  password;

    public User() {}

    public User(Integer userId, String firstName, String lastName,
                String email, String password) {
        this.userId    = userId;
        this.firstName = firstName;
        this.lastName  = lastName;
        this.email     = email;
        this.password  = password;
    }

    public Integer getUserId()            { return userId; }
    public void    setUserId(Integer v)   { this.userId = v; }
    public String  getFirstName()         { return firstName; }
    public void    setFirstName(String v) { this.firstName = v; }
    public String  getLastName()          { return lastName; }
    public void    setLastName(String v)  { this.lastName = v; }
    public String  getEmail()             { return email; }
    public void    setEmail(String v)     { this.email = v; }
    public String  getPassword()          { return password; }
    public void    setPassword(String v)  { this.password = v; }
}
