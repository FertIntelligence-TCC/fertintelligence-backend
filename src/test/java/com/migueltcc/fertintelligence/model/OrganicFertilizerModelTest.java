package com.migueltcc.fertintelligence.model;

import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.OrganicFertilizerModel;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrganicFertilizerModelTest {

    @Test
    void toDtoCalculatesOrganicCarbonFromOrganicMatterRoundedToOneDecimal() {
        OrganicFertilizerModel fertilizer = OrganicFertilizerModel.builder()
                .teorMateriaOrganicaPercentual(43.1)
                .build();

        assertThat(fertilizer.toDto().getTeorCarbonoOrganicoPercentual()).isEqualTo(25.0);
    }
}
