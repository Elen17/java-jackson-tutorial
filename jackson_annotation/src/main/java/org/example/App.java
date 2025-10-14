import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.example.model.Person;

void main() {
    String personJsonWithNull = """
            {
                "id": 12,
                "name": "John Doe",
                "enabled": null
            }
            """.stripIndent();


    String personJsonEnabled = """
            {
                "id": 12,
                "name": "John Doe",
                "enabled": "true"
            }
            """.stripIndent();


    String personJsonDisabled = """
            {
                "id": 12,
                "name": "John Doe",
                "enabled": "false"
            }
            """.stripIndent();
    ObjectMapper mapper = new ObjectMapper();
    ObjectReader reader = mapper.readerFor(Person.class);
    ObjectWriter writer = mapper.writerFor(Person.class);

    try {
        Person personWithNull = reader.readValue(personJsonWithNull);
        System.out.println(personWithNull);
        System.out.println();

        Person personEnabled = mapper.readValue(personJsonEnabled, Person.class);
        System.out.println(personEnabled);
        System.out.println();

        Person personDisabled = reader.readValue(personJsonDisabled);
        System.out.println(personDisabled);

        String personToJson = writer.writeValueAsString(personWithNull);

        System.out.println(personToJson);

    } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
    }
}
