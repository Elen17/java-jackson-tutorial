package org.example.custom_serializer.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Customer {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private Address shippingAddress;

    public Customer() {
    }

    public Customer(
            String id,
            String fullName,
            String email,
            Address shippingAddress) {
        this.id = id;
        String[] names = fullName != null ? fullName.split("\\s+", 2) : new String[0];

        this.firstName = names.length > 0 ? names[0] : "";
        this.lastName = names.length > 1 ? names[1] : "";

        this.email = email;
        this.shippingAddress = shippingAddress;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Address getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(Address shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Customer) obj;
        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.firstName, that.firstName) &&
                Objects.equals(this.lastName, that.lastName) &&
                Objects.equals(this.email, that.email) &&
                Objects.equals(this.shippingAddress, that.shippingAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, email, shippingAddress);
    }

    @Override
    public String toString() {
        return "Customer[" +
                "id=" + id + ", " +
                "firstName=" + lastName + ", " +
                "lastName=" + lastName + ", " +
                "email=" + email + ", " +
                "shippingAddress=" + shippingAddress + ']';
    }

}
