package com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels;

import com.migueltcc.fertintelligence.composedAttributes.fertilizers.NPKrelation;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.formulatedMineralFertilizer.FormulatedMineralFertilizerResponseDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FormulatedMineralFertilizerModelTest {

    @Test
    void toDtoExpressesNpkRelationDividedBySmallestQuantity() {
        FormulatedMineralFertilizerModel fertilizer = FormulatedMineralFertilizerModel.builder()
                .relation(new NPKrelation(4.0, 14.0, 18.0))
                .build();

        FormulatedMineralFertilizerResponseDto dto = fertilizer.toDto();

        assertEquals(1.0, dto.getRelation().getN());
        assertEquals(3.5, dto.getRelation().getP());
        assertEquals(4.5, dto.getRelation().getK());
    }

    @Test
    void toDtoKeepsDecimalRelationWhenSmallestQuantityIsOne() {
        FormulatedMineralFertilizerModel fertilizer = FormulatedMineralFertilizerModel.builder()
                .relation(new NPKrelation(1.0, 3.5, 2.0))
                .build();

        FormulatedMineralFertilizerResponseDto dto = fertilizer.toDto();

        // A regra anterior expressava 1-3.5-2 como 2-7-4; sem regra de negocio
        // documentada no codigo para manter inteiros, prevalece a divisao pelo menor teor.
        assertEquals(1.0, dto.getRelation().getN());
        assertEquals(3.5, dto.getRelation().getP());
        assertEquals(2.0, dto.getRelation().getK());
    }

    @Test
    void toDtoExpressesNpkRelationWithNitrogenAndPotassiumAboveSmallestQuantity() {
        FormulatedMineralFertilizerModel fertilizer = FormulatedMineralFertilizerModel.builder()
                .relation(new NPKrelation(10.0, 5.0, 20.0))
                .build();

        FormulatedMineralFertilizerResponseDto dto = fertilizer.toDto();

        assertEquals(2.0, dto.getRelation().getN());
        assertEquals(1.0, dto.getRelation().getP());
        assertEquals(4.0, dto.getRelation().getK());
    }

    @Test
    void toDtoExpressesNpkRelationWithPotassiumAsSmallestQuantity() {
        FormulatedMineralFertilizerModel fertilizer = FormulatedMineralFertilizerModel.builder()
                .relation(new NPKrelation(20.0, 30.0, 5.0))
                .build();

        FormulatedMineralFertilizerResponseDto dto = fertilizer.toDto();

        assertEquals(4.0, dto.getRelation().getN());
        assertEquals(6.0, dto.getRelation().getP());
        assertEquals(1.0, dto.getRelation().getK());
    }
}
