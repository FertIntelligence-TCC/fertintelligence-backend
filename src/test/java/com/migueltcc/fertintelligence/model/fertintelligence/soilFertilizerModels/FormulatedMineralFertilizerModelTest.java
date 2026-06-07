package com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels;

import com.migueltcc.fertintelligence.composedAttributes.fertilizers.Formulate;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.formulatedMineralFertilizer.FormulatedMineralFertilizerResponseDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FormulatedMineralFertilizerModelTest {

    @Test
    void toDtoCalculatesNpkRelationDividingBySmallestFormulateValue() {
        FormulatedMineralFertilizerModel fertilizer = FormulatedMineralFertilizerModel.builder()
                .formulate(new Formulate(10, 5, 20))
                .build();

        FormulatedMineralFertilizerResponseDto dto = fertilizer.toDto();

        assertEquals(2.0, dto.getRelation().getN());
        assertEquals(1.0, dto.getRelation().getP());
        assertEquals(4.0, dto.getRelation().getK());
    }

    @Test
    void toDtoAllowsDecimalNpkRelationValues() {
        FormulatedMineralFertilizerModel fertilizer = FormulatedMineralFertilizerModel.builder()
                .formulate(new Formulate(4, 14, 18))
                .build();

        FormulatedMineralFertilizerResponseDto dto = fertilizer.toDto();

        assertEquals(1.0, dto.getRelation().getN());
        assertEquals(3.5, dto.getRelation().getP());
        assertEquals(4.5, dto.getRelation().getK());
    }

    @Test
    void toDtoLimitsNpkRelationValuesToTwoDecimalPlaces() {
        FormulatedMineralFertilizerModel fertilizer = FormulatedMineralFertilizerModel.builder()
                .formulate(new Formulate(3, 10, 20))
                .build();

        FormulatedMineralFertilizerResponseDto dto = fertilizer.toDto();

        assertEquals(1.0, dto.getRelation().getN());
        assertEquals(3.33, dto.getRelation().getP());
        assertEquals(6.67, dto.getRelation().getK());
    }

    @Test
    void toDtoCalculatesNpkRelationWhenSmallestValueIsPotassium() {
        FormulatedMineralFertilizerModel fertilizer = FormulatedMineralFertilizerModel.builder()
                .formulate(new Formulate(20, 30, 5))
                .build();

        FormulatedMineralFertilizerResponseDto dto = fertilizer.toDto();

        assertEquals(4.0, dto.getRelation().getN());
        assertEquals(6.0, dto.getRelation().getP());
        assertEquals(1.0, dto.getRelation().getK());
    }
}
