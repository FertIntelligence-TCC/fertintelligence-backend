package com.migueltcc.fertintelligence.composedAttributes.physicalAnalysis;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class PhysicalAnalysisUnitConverter implements AttributeConverter<PhysicalAnalysisUnit, String> {

    @Override
    public String convertToDatabaseColumn(PhysicalAnalysisUnit attribute) {
        return attribute != null ? attribute.getSymbol() : null;
    }

    @Override
    public PhysicalAnalysisUnit convertToEntityAttribute(String dbData) {
        return PhysicalAnalysisUnit.fromValue(dbData);
    }
}
