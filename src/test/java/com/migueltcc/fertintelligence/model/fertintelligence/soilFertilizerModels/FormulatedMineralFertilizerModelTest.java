package com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels;

import com.migueltcc.fertintelligence.composedAttributes.fertilizers.NPKrelation;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.formulatedMineralFertilizer.FormulatedMineralFertilizerResponseDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FormulatedMineralFertilizerModelTest {

    @Test
    void toDtoExpressesDecimalNpkRelationAsIntegers() {
        FormulatedMineralFertilizerModel fertilizer = FormulatedMineralFertilizerModel.builder()
                .relation(new NPKrelation(1.0, 3.5, 2.0))
                .build();

        FormulatedMineralFertilizerResponseDto dto = fertilizer.toDto();

        assertEquals(2.0, dto.getRelation().getN());
        assertEquals(7.0, dto.getRelation().getP());
        assertEquals(4.0, dto.getRelation().getK());
    }

    @Test
    void toDtoExpressesRepeatingDecimalNpkRelationAsIntegers() {
        FormulatedMineralFertilizerModel fertilizer = FormulatedMineralFertilizerModel.builder()
                .relation(new NPKrelation(1.0, 0.3333333333333333, 0.5))
                .build();

        FormulatedMineralFertilizerResponseDto dto = fertilizer.toDto();

        assertEquals(6.0, dto.getRelation().getN());
        assertEquals(2.0, dto.getRelation().getP());
        assertEquals(3.0, dto.getRelation().getK());
    }
}
