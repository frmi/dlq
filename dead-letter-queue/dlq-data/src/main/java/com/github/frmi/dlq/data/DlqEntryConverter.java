package com.github.frmi.dlq.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.frmi.dlq.api.data.DlqEntry;
import com.github.frmi.dlq.data.exception.DlqDataException;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class DlqEntryConverter implements AttributeConverter<DlqEntry, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(DlqEntry dlqEntry) {

        String dlqEntryJson;
        try {
            dlqEntryJson = objectMapper.writeValueAsString(dlqEntry);
        } catch (final JsonProcessingException e) {
            throw new DlqDataException("Error converting DlqEntry to JSON", e);
        }

        return dlqEntryJson;
    }

    @Override
    public DlqEntry convertToEntityAttribute(String dlqEntryJson) {

        DlqEntry dlqEntry;
        try {
            dlqEntry = objectMapper.readValue(dlqEntryJson, DlqEntry.class);
        } catch (final JsonProcessingException e) {
            throw new DlqDataException("Error converting JSON to DlqEntry", e);
        }

        return dlqEntry;
    }
}
