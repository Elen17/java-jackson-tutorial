package org.example.deserialization.model;

import java.util.Objects;

public  class Customer {
    private  String id;
    private  String name;
    private  String email;
    private  Address shippingAddress;

    public Customer() {
    }

    public Customer(
            String id,
            String name,
            String email,
            Address shippingAddress) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.shippingAddress = shippingAddress;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
                Objects.equals(this.name, that.name) &&
                Objects.equals(this.email, that.email) &&
                Objects.equals(this.shippingAddress, that.shippingAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, shippingAddress);
    }

    @Override
    public String toString() {
        return "Customer[" +
                "id=" + id + ", " +
                "name=" + name + ", " +
                "email=" + email + ", " +
                "shippingAddress=" + shippingAddress + ']';
    }

}
