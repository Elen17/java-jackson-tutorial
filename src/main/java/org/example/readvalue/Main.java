import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.readvalue.Name;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static java.lang.System.out;

void main() {

    ObjectMapper mapper = new ObjectMapper();


    try {
        Path filePath = Path.of(ClassLoader.getSystemResource(FILE_NAME).toURI());
        Path invalidFilePath = Path.of(ClassLoader.getSystemResource(INVALID_FILE).toURI());
        out.printf("%nDeserializing from file: %s%n%n", FILE_NAME);

        Name name = mapper.readValue(filePath.toFile(), Name.class);
        out.printf(FIRST_NAME_S_LAST_NAME_S_N,
                name.firstName(), name.lastName());
        out.println();

        // Reading from String
        out.printf("Deserializing from string: %s%n%n", JSON_NAME);
        @SuppressWarnings("unchecked")
        Map<String, String> nameMap = (Map<String, String>) mapper.readValue(JSON_NAME, Map.class);
        out.printf(FIRST_NAME_S_LAST_NAME_S_N,
                nameMap.get("firstName"), nameMap.get("lastName"));

        // Reading from JSON array

        out.printf("%n%s%n%n", "Deserializing from JSON array");

        Name[] names = mapper.readValue(JSON_ARRAY, Name[].class); // as a result we get an array of Name's
        for (Name value : names) {
            out.printf(FIRST_NAME_S_LAST_NAME_S_N,
                    value.firstName(), value.lastName());
        }

        // using Reader
        out.printf("%n%s%n%n", "Deserializing from Reader");

        Reader reader = new StringReader(JSON_NAME);
        Name nameReader = mapper.readValue(reader, Name.class);
        out.printf(FIRST_NAME_S_LAST_NAME_S_N,
                nameReader.firstName(), nameReader.lastName());

        // casting an JSOn Array to the List
        out.printf("%n%s%n%n", "Deserializing from JSON array to List");

        Path nameListFilePath = Path.of(ClassLoader.getSystemResource(NAME_LIST_FILE_NAME).toURI());

        List<Name> names2 = mapper.readValue(nameListFilePath.toFile(), new TypeReference<>() {
        }); // as a result we get List of Name's
        for (Name value : names2) {
            out.printf(FIRST_NAME_S_LAST_NAME_S_N,
                    value.firstName(), value.lastName());
        }

        // should fail while finding age (not defined) property

        out.printf("%n%s%n%n", "Deserializing from Reader");
        try (Reader invalidReader = new FileReader(invalidFilePath.toFile())) {
            // should fail
            Name invalidNameReader = mapper.readValue(invalidReader, Name.class);

            out.printf(FIRST_NAME_S_LAST_NAME_S_N,
                    invalidNameReader.firstName(), invalidNameReader.lastName());

            // casting an JSON Array to the List
            out.printf("%n%s%n%n", "Deserializing from JSON array to List");
        } catch (JsonProcessingException e) {
            out.printf("Error while deserializing: %s", e.getMessage());
        }


        // should not fail while finding age (not defined) property
        // configured to ignore unknown properties
        out.println();

        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        out.printf("%n%s%n%n", "Deserializing from Reader");
        try (Reader invalidReader = new FileReader(invalidFilePath.toFile())) {
            // should not fail
            Name invalidNameReader = mapper.readValue(invalidReader, Name.class);

            out.printf(FIRST_NAME_S_LAST_NAME_S_N,
                    invalidNameReader.firstName(), invalidNameReader.lastName());
        } catch (JsonProcessingException e) {
            out.printf("Error while deserializing: %s", e.getMessage());
        }

    } catch (IOException | URISyntaxException e) {
        out.printf("Error: %s", e.getMessage());
    }

}

public static final String FIRST_NAME_S_LAST_NAME_S_N = """
        First name: %s
        Last name: %s%n
        """.stripIndent();

public static final String JSON_NAME = """
        {
            "firstName": "Jane",
            "lastName": "Doe"
        }
        """.stripIndent();

public static final String JSON_ARRAY = "[{\"firstName\": \"James\", \"lastName\": \"Mayer\"}, {\"firstName\": \"Audrey\", \"lastName\": \"Bolton\"}]";


public static final String FILE_NAME = "name.json";
public static final String NAME_LIST_FILE_NAME = "nameList.json";
public static final String INVALID_FILE = "invalidJson.json";
