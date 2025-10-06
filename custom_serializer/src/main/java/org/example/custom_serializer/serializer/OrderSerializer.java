package org.example.custom_serializer.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.example.custom_serializer.model.Order;

import java.io.IOException;

public class OrderSerializer extends StdSerializer<Order> {
    public OrderSerializer(Class<Order> clazz) {
        super(clazz);
    }

    @Override
    public void serialize(Order order, JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        // order
        //Customer customer;
        //List<OrderItem> items;
        // orderId
        jsonGenerator.writeStringField("ID", order.getOrderId());
        // totalAmount
        jsonGenerator.writeNumberField("amount", order.getTotalAmount());
        // customer
        jsonGenerator.writeObjectField("customer", order.getCustomer());
        // items
        jsonGenerator.writeObjectField("items", order.getItems());
        // totalAmount
        jsonGenerator.writeEndObject();

    }
}
