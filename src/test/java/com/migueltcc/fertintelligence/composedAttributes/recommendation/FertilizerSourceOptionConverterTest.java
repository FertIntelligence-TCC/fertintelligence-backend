package com.migueltcc.fertintelligence.composedAttributes.recommendation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FertilizerSourceOptionConverterTest {

    private final FertilizerSourceOptionConverter converter = new FertilizerSourceOptionConverter();

    @Test
    void convertToDatabaseColumn_UsesLegacyPortugueseValuesAcceptedByRecommendationsConstraint() {
        assertEquals("PRIVADAS", converter.convertToDatabaseColumn(FertilizerSourceOption.PRIVATE));
        assertEquals("PUBLICAS", converter.convertToDatabaseColumn(FertilizerSourceOption.PUBLIC));
        assertEquals("PADRAO", converter.convertToDatabaseColumn(FertilizerSourceOption.DEFAULT));
        assertEquals("AMBAS", converter.convertToDatabaseColumn(FertilizerSourceOption.BOTH));
    }

    @Test
    void convertToEntityAttribute_AcceptsLegacyPortugueseAndEnglishValues() {
        assertEquals(FertilizerSourceOption.PRIVATE, converter.convertToEntityAttribute("PRIVADAS"));
        assertEquals(FertilizerSourceOption.PUBLIC, converter.convertToEntityAttribute("PUBLIC"));
        assertEquals(FertilizerSourceOption.DEFAULT, converter.convertToEntityAttribute("PADRAO"));
        assertEquals(FertilizerSourceOption.BOTH, converter.convertToEntityAttribute("BOTH"));
    }
}
