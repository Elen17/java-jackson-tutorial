package org.example.deserialization.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.example.deserialization.model.Address;
import org.example.deserialization.model.Customer;
import org.example.deserialization.model.Order;
import org.example.deserialization.model.OrderItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OrderDeserializer extends StdDeserializer<Order> {

    public OrderDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Order deserialize(JsonParser p, DeserializationContext context)
            throws IOException, JsonProcessingException {

        Order order = new Order();
        ObjectCodec codec = p.getCodec();
        JsonNode node = codec.readTree(p);

        // Deserialize simple fields
        order.setOrderId(node.get("orderId").asText());
        order.setTotalAmount(node.get("totalAmount").asDouble());

        // Deserialize Customer
        JsonNode customerNode = node.get("customer");
        Customer customer = new Customer();
        customer.setId(customerNode.get("id").asText());
        customer.setName(customerNode.get("name").asText());
        customer.setEmail(customerNode.get("email").asText());

        // Deserialize Address within Customer
        JsonNode addressNode = customerNode.get("shippingAddress");
        Address address = new Address();
        address.setStreet(addressNode.get("street").asText());
        address.setCity(addressNode.get("city").asText());
        address.setZipCode(addressNode.get("zipCode").asText());
        address.setCountry(addressNode.get("country").asText());
        customer.setShippingAddress(address);

        order.setCustomer(customer);

        // Deserialize OrderItems
        List<OrderItem> items = new ArrayList<>();
        JsonNode itemsNode = node.get("items");
        for (JsonNode itemNode : itemsNode) {
            OrderItem item = new OrderItem();
            item.setProductId(itemNode.get("productId").asText());
            item.setProductName(itemNode.get("productName").asText());
            item.setQuantity(itemNode.get("quantity").asInt());
            item.setUnitPrice(itemNode.get("unitPrice").asDouble());
            items.add(item);
        }
        order.setItems(items);

        return order;
    }
}