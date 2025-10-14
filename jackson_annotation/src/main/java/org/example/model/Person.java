package org.example.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.example.deserializer.OptimizedBooleanDeserializer;

public record Person(
        @JsonProperty("id")
        long personId,
        String name,
        @JsonDeserialize(using = OptimizedBooleanDeserializer.class)
        @JsonProperty()
        Boolean enabled) {

    @JsonValue
//    @JsonRawValue
    /**
     * with @JsonRawValue we can return a raw JSON value => "Elen
     * without @JsonRawValue all backslashes are escaped => "\"Elen"
     */
    public String toJson() {
        return "\"Elen";
    }
}
