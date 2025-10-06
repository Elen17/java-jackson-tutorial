package org.example.custom_serializer.model;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public final class Order {
    private String orderId;
    private Customer customer;
    private List<OrderItem> items;
    private double totalAmount;
    private LocalDate orderDate;

    public Order(
            String orderId,
            Customer customer,
            List<OrderItem> items,
            double totalAmount,
            LocalDate orderDate) {
        this.orderId = orderId;
        this.customer = customer;
        this.items = items;
        this.totalAmount = totalAmount;
        this.orderDate = orderDate;
    }


    public Order() {
        super();
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Order) obj;
        return Objects.equals(this.orderId, that.orderId) &&
                Objects.equals(this.customer, that.customer) &&
                Objects.equals(this.orderDate, that.orderDate) &&
                Objects.equals(this.items, that.items) &&
                Double.doubleToLongBits(this.totalAmount) == Double.doubleToLongBits(that.totalAmount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, customer, orderDate, items, totalAmount);
    }

    @Override
    public String toString() {
        return "Order[" +
                "orderId=" + orderId + ", " +
                "customer=" + customer + ", " +
                "orderDate=" + orderDate + ", " +
                "items=" + items + ", " +
                "totalAmount=" + totalAmount + ']';
    }

}
