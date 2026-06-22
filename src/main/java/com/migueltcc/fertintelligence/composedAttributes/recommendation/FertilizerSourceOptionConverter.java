package com.migueltcc.fertintelligence.composedAttributes.recommendation;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class FertilizerSourceOptionConverter implements AttributeConverter<FertilizerSourceOption, String> {

    @Override
    public String convertToDatabaseColumn(FertilizerSourceOption attribute) {
        return attribute == null ? null : attribute.toPersistedValue();
    }

    @Override
    public FertilizerSourceOption convertToEntityAttribute(String dbData) {
        return FertilizerSourceOption.fromPersistedValue(dbData);
    }
}
