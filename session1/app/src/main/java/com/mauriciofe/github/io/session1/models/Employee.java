package com.mauriciofe.github.io.session1.models;

public class Employee {
    private int Id ;
    private String FirstName ;
    private String LastName ;
    private String  Phone ;

    public Employee() {
    }

    public Employee(int id, String firstName, String lastName, String phone) {
        Id = id;
        FirstName = firstName;
        LastName = lastName;
        Phone = phone;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }
}
