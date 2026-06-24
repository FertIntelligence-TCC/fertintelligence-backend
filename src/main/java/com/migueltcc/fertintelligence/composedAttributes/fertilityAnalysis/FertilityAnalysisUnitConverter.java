package com.migueltcc.fertintelligence.composedAttributes.fertilityAnalysis;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class FertilityAnalysisUnitConverter implements AttributeConverter<FertilityAnalysisUnit, String> {

    @Override
    public String convertToDatabaseColumn(FertilityAnalysisUnit attribute) {
        return attribute != null ? attribute.getSymbol() : null;
    }

    @Override
    public FertilityAnalysisUnit convertToEntityAttribute(String dbData) {
        return FertilityAnalysisUnit.fromValue(dbData);
    }
}
