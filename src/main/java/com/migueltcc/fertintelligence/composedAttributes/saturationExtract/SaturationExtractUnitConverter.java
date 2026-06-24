package com.migueltcc.fertintelligence.composedAttributes.saturationExtract;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class SaturationExtractUnitConverter implements AttributeConverter<SaturationExtractUnit, String> {

    @Override
    public String convertToDatabaseColumn(SaturationExtractUnit attribute) {
        return attribute != null ? attribute.getSymbol() : null;
    }

    @Override
    public SaturationExtractUnit convertToEntityAttribute(String dbData) {
        return SaturationExtractUnit.fromValue(dbData);
    }
}
