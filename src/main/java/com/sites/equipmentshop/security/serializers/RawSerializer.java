package com.sites.equipmentshop.security.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Map;

public class RawSerializer extends JsonSerializer<Map<String, String>> {

    @Override
    public void serialize(Map<String, String> value,
                          JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        for (Map.Entry<String, String> e : value.entrySet()) {
            jsonGenerator.writeFieldName(e.getKey());
            jsonGenerator.writeRawValue(e.getValue());
        }
        jsonGenerator.writeEndObject();
    }
}
