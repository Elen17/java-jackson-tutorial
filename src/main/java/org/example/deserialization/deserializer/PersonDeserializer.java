package org.example.deserialization.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.example.deserialization.model.Person;

import java.io.IOException;
import java.util.Date;

public class PersonDeserializer extends StdDeserializer<Person> {

    public PersonDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Person deserialize(JsonParser parser,
                              DeserializationContext context) throws IOException, JacksonException {
        Person person = new Person();
        JsonToken jsonToken;
        while (!parser.isClosed()) {
            jsonToken = parser.nextToken(); // returns type of next token
            if (JsonToken.FIELD_NAME.equals(jsonToken)) { // if the current token is a field name ("name", "email", "birthDate")
                String fieldName = parser.getCurrentName();

                jsonToken = parser.nextToken(); //  move to the value token; VALUE_STRING or VALUE_NUMBER_INT in this case; to get the value

                if ("name".equals(fieldName)) {
                    person.setName(parser.getValueAsString());
                } else if ("email".equals(fieldName)) {
                    person.setEmail(parser.getValueAsString());
                } else if ("birthDate".equals(fieldName)) {
                    person.setBirthDate(new Date(parser.getLongValue()));
                }
            }

        }

        return person;
    }
}
