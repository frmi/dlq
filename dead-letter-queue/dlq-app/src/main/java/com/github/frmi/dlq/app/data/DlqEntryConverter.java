package com.github.frmi.dlq.app.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.frmi.dlq.api.data.DlqEntry;

import javax.persistence.AttributeConverter;
import java.io.IOException;

public class DlqEntryConverter implements AttributeConverter<DlqEntry, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(DlqEntry dlqEntry) {

        String dlqEntryJson = null;
        try {
            dlqEntryJson = objectMapper.writeValueAsString(dlqEntry);
        } catch (final JsonProcessingException e) {
//            logger.error("JSON writing error", e);
        }

        return dlqEntryJson;
    }

    @Override
    public DlqEntry convertToEntityAttribute(String dlqEntryJson) {

        DlqEntry dlqEntry = null;
        try {
            dlqEntry = objectMapper.readValue(dlqEntryJson, DlqEntry.class);
        } catch (final IOException e) {
//            logger.error("JSON reading error", e);
        }

        return dlqEntry;
    }
}
