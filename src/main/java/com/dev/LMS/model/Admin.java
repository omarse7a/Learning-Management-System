package com.dev.LMS.model;

import jakarta.persistence.Entity;

@Entity
public class Admin extends User{
    public Admin() {}

    public Admin(String name, String email) {
        super(name, email, Role.ADMIN);
    }

}
