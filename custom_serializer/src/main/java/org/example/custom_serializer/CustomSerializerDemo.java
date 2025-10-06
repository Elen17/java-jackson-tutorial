package org.example.custom_serializer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.example.custom_serializer.deserializer.OrderDeserializer;
import org.example.custom_serializer.model.Customer;
import org.example.custom_serializer.model.Order;
import org.example.custom_serializer.serializer.CustomerSerializer;
import org.example.custom_serializer.serializer.OrderSerializer;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

public class CustomSerializerDemo {
    private static final Logger LOGGER = Logger.getLogger(CustomSerializerDemo.class.getName()); ;
    public static void main(String ... args) {
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
            objectMapper.writeValue(new File("serialized_order.json"), order);

            @SuppressWarnings("unchecked")
            List<Order> orders = (List<Order>) objectMapper.readValue(ClassLoader.getSystemResourceAsStream("orders.json"), List.class);

            LOGGER.info(String.format("Orders: %s", orders));

            objectMapper.writeValue(new File("serialized_orders.json"), orders);



        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
