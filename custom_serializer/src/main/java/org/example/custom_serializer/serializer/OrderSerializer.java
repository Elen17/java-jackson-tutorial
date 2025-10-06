package org.example.custom_serializer.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.example.custom_serializer.model.Order;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

public class OrderSerializer extends StdSerializer<Order> {

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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
        // orderDate
        jsonGenerator.writeStringField("orderDate",
                Optional.ofNullable(order.getOrderDate())
                        .map(FORMATTER::format)
                        .orElse(null)
        );
        // totalAmount
        jsonGenerator.writeEndObject();

    }
}
