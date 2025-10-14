package org.example.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class OptimizedBooleanDeserializer extends JsonDeserializer<Boolean> {

    @Override
    public Boolean deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException, JacksonException {
        String value = parser.getValueAsString();
        if (value == null) {
            return null;
        }
        return Boolean.parseBoolean(value);
    }

}
