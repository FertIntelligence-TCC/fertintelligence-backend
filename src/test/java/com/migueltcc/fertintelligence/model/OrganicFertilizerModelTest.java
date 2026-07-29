package com.migueltcc.fertintelligence.model;

import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.OrganicFertilizerModel;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrganicFertilizerModelTest {

    @Test
    void toDtoCalculatesOrganicCarbonAndCarbonNitrogenRatioWithoutPrematureRounding() {
        OrganicFertilizerModel fertilizer = OrganicFertilizerModel.builder()
                .teorMateriaOrganicaPercentual(34.48)
                .N(2.0)
                .build();

        assertThat(fertilizer.toDto().getTeorCarbonoOrganicoPercentual()).isEqualTo(20.0);
        assertThat(fertilizer.toDto().getRelacaoCarbonoNitrogenio()).isEqualTo(10.0);
    }

    @Test
    void carbonAndRatioPreserveNullAndExplicitZeroWithoutNanOrInfinity() {
        var absent = OrganicFertilizerModel.builder().build().toDto();
        assertThat(absent.getTeorCarbonoOrganicoPercentual()).isNull();
        assertThat(absent.getRelacaoCarbonoNitrogenio()).isNull();

        var zeroMatter = OrganicFertilizerModel.builder()
                .teorMateriaOrganicaPercentual(0d).N(2d).build().toDto();
        assertThat(zeroMatter.getTeorCarbonoOrganicoPercentual()).isZero();
        assertThat(zeroMatter.getRelacaoCarbonoNitrogenio()).isZero();

        var zeroNitrogen = OrganicFertilizerModel.builder()
                .teorMateriaOrganicaPercentual(34.48d).N(0d).build().toDto();
        assertThat(zeroNitrogen.getTeorCarbonoOrganicoPercentual()).isEqualTo(20d);
        assertThat(zeroNitrogen.getRelacaoCarbonoNitrogenio()).isNull();
    }
}
