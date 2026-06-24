package com.migueltcc.fertintelligence.composedAttributes.fertilizationTables;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class FertilizationTableUnitConverter implements AttributeConverter<FertilizationTableUnit, String> {

    @Override
    public String convertToDatabaseColumn(FertilizationTableUnit attribute) {
        return attribute != null ? attribute.getSymbol() : null;
    }

    @Override
    public FertilizationTableUnit convertToEntityAttribute(String dbData) {
        return FertilizationTableUnit.fromValue(dbData);
    }
}
