package com.migueltcc.fertintelligence.dto.recommendation.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.CriterioCalagem;

import java.io.IOException;
import java.util.Locale;

public class NullableLimingCriteriaDeserializer extends JsonDeserializer<CriterioCalagem> {

    @Override
    public CriterioCalagem deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String value = parser.getValueAsString();
        if (value == null || value.isBlank()) {
            return null;
        }
        return CriterioCalagem.valueOf(value.trim().toUpperCase(Locale.ROOT));
    }
}
