package com.migueltcc.fertintelligence.model.fertintelligence.extractAnalysisModels;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PhysicalAnalysisExtractModelTest {

    @Test
    void recalculateComputedFieldsUsesSystemFormulas() {
        PhysicalAnalysisExtractModel model = PhysicalAnalysisExtractModel.builder()
                .densidadeAparente(1.2)
                .densidadeReal(2.6)
                .porosidadeTotal(999.0)
                .umidadeCapacidadeCampo(28.0)
                .umidadePontoMurchaPermanente(15.0)
                .aguaDisponivel(999.0)
                .build();

        model.recalculateComputedFields();

        assertEquals(53.84615384615385, model.getPorosidadeTotal(), 0.000001);
        assertEquals(13.0, model.getAguaDisponivel(), 0.000001);
    }
}
