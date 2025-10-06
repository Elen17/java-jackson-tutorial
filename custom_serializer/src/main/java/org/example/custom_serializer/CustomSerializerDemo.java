package org.example.custom_serializer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.example.custom_serializer.deserializer.OrderDeserializer;
import org.example.custom_serializer.model.Customer;
import org.example.custom_serializer.model.Order;
import org.example.custom_serializer.serializer.CustomerSerializer;
import org.example.custom_serializer.serializer.OrderSerializer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

public class CustomSerializerDemo {
    private static final Logger LOGGER = Logger.getLogger(CustomSerializerDemo.class.getName());
    ;

    static void main(String... args) {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        OrderSerializer orderSerializer = new OrderSerializer(Order.class);
        OrderDeserializer orderDeserializer = new OrderDeserializer(Order.class);
        CustomerSerializer customerSerializer = new CustomerSerializer(Customer.class);
        module.addDeserializer(Order.class, orderDeserializer);
        module.addSerializer(Order.class, orderSerializer);
        module.addSerializer(Customer.class, customerSerializer);

        objectMapper.registerModule(module);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false);

        try {
            Order order = objectMapper.readValue(ClassLoader.getSystemResourceAsStream("order.json"), Order.class);

            LOGGER.info(String.format("Order: %s", order));

            // write to json file
            objectMapper.writeValue(Path.of(ClassLoader.getSystemResource("serialized_order.json").toURI()).toFile(), order);

            @SuppressWarnings("unchecked")
            // in case of getting List<Order> from json file; while deserializing we need to use TypeReference
            // because List.class is not a parameterized type
            // so while serializing we need to use TypeReference so our custom serializer worked
            List<Order> orders = (List<Order>) objectMapper.readValue(ClassLoader.getSystemResourceAsStream("orders.json"), List.class);

            LOGGER.info(String.format("Orders: %s", orders));

            objectMapper.writeValue(Path.of(ClassLoader.getSystemResource("serialized_orders_not_custom.json").toURI()).toFile(), orders);

            orders = (List<Order>) objectMapper.readValue(ClassLoader.getSystemResourceAsStream("orders.json"), new TypeReference<List<Order>>() {
            });

            objectMapper.writeValue(Path.of(ClassLoader.getSystemResource("serialized_orders_custom.json").toURI()).toFile(), orders);

        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}
