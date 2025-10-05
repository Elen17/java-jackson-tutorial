package org.example.deserialization.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.example.deserialization.deserializer.PersonDeserializer;

import java.util.Date;

public class Person {
    private String name;
    private String email;
    
//    @JsonDeserialize(using = PersonDeserializer.class)
    private Date birthDate;
    
    // Default constructor
    public Person() {}
    
    // Getters and setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }
//
    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", birthDate=" + birthDate +
                '}';
    }
}
