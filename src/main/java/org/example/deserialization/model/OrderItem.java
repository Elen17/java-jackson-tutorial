package org.example.deserialization.model;

import java.util.Objects;

public class OrderItem {
    private String productId;
    private String productName;
    private int quantity;
    private double unitPrice;


    public OrderItem() {
    }

    public OrderItem(
            String productId,
            String productName,
            int quantity,
            double unitPrice) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (OrderItem) obj;
        return Objects.equals(this.productId, that.productId) &&
                Objects.equals(this.productName, that.productName) &&
                this.quantity == that.quantity &&
                Double.doubleToLongBits(this.unitPrice) == Double.doubleToLongBits(that.unitPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, productName, quantity, unitPrice);
    }

    @Override
    public String toString() {
        return "OrderItem[" +
                "productId=" + productId + ", " +
                "productName=" + productName + ", " +
                "quantity=" + quantity + ", " +
                "unitPrice=" + unitPrice + ']';
    }

}
