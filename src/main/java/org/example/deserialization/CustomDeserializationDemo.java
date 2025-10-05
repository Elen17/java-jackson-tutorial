import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.example.deserialization.deserializer.OrderDeserializer;
import org.example.deserialization.deserializer.PersonDeserializer;
import org.example.deserialization.model.Order;
import org.example.deserialization.model.Person;

void main () {
    ObjectMapper objectMapper = new ObjectMapper();
    SimpleModule simpleModule = new SimpleModule();
    // adding custom deserializers
    PersonDeserializer personDeserializer = new PersonDeserializer(Person.class);
    OrderDeserializer orderDeserializer = new OrderDeserializer(Order.class);
    simpleModule.addDeserializer(Person.class, personDeserializer);
    simpleModule.addDeserializer(Order.class, orderDeserializer);

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    objectMapper.setDateFormat(simpleDateFormat);

    objectMapper.registerModule(simpleModule);

    String json = """
            {
                "name": "John Doe",
                "email": "john.doe@example.com",
                "birthDate": 1835072800000
            }
            """;
    try {
        Person person = objectMapper.readValue(json, Person.class);
        System.out.println(person);

//        objectMapper.writeValue(System.out, person);

        // order deserialization from json file
        Path orderJsonPath = Path.of(ClassLoader.getSystemResource("order.json").toURI());

        Order order = objectMapper.readValue(orderJsonPath.toFile(), Order.class);
        System.out.printf("Order: %s", order.toString());
    } catch (Exception e) {
        e.printStackTrace();
    }
}