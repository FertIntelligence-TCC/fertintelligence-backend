package com.migueltcc.fertintelligence.model;

import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.GreenFertilizerModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.OrganicFertilizerModel;
import com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels.OrganoMineralFertilizerModel;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FertilizerExtendedFieldsModelTest {

    @Test
    void greenMoistureAndFourthYearPreserveNullZeroAndDecimalsWithoutChangingCarbonNitrogenRatio() {
        var historical = GreenFertilizerModel.builder().C(18d).N(3d).build().toDto();
        assertThat(historical.getUmidadeIncorporacaoPercentual()).isNull();
        assertThat(historical.getTaxaMineralizacaoQuartoAnoPercentual()).isNull();
        assertThat(historical.getC() / historical.getN()).isEqualTo(6d);

        var current = GreenFertilizerModel.builder()
                .C(18d).N(3d).umidadeIncorporacaoPercentual(0d)
                .taxaMineralizacaoQuartoAnoPercentual(12.75d).build().toDto();
        assertThat(current.getUmidadeIncorporacaoPercentual()).isZero();
        assertThat(current.getTaxaMineralizacaoQuartoAnoPercentual()).isEqualTo(12.75d);
        assertThat(current.getC() / current.getN()).isEqualTo(6d);
    }

    @Test
    void organicFourthYearAndEveryHeavyMetalPreserveNullZeroAndDecimals() {
        var historical = OrganicFertilizerModel.builder().build().toDto();
        assertThat(historical.getTaxaMineralizacaoQuartoAnoPercentual()).isNull();
        assertThat(historical.getArsenioMgKg()).isNull();
        assertThat(historical.getSelenioMgKg()).isNull();

        var current = OrganicFertilizerModel.builder()
                .taxaMineralizacaoQuartoAnoPercentual(7.5d)
                .arsenioMgKg(0d).cadmioMgKg(0.12d).cromioMgKg(1.23d)
                .chumboMgKg(2.34d).mercurioMgKg(0.045d).niquelMgKg(3.45d).selenioMgKg(0.56d)
                .build().toDto();
        assertThat(current.getTaxaMineralizacaoQuartoAnoPercentual()).isEqualTo(7.5d);
        assertThat(current.getArsenioMgKg()).isZero();
        assertThat(current.getCadmioMgKg()).isEqualTo(0.12d);
        assertThat(current.getCromioMgKg()).isEqualTo(1.23d);
        assertThat(current.getChumboMgKg()).isEqualTo(2.34d);
        assertThat(current.getMercurioMgKg()).isEqualTo(0.045d);
        assertThat(current.getNiquelMgKg()).isEqualTo(3.45d);
        assertThat(current.getSelenioMgKg()).isEqualTo(0.56d);
    }

    @Test
    void organomineralFourYearsPreserveHistoricalNullsAndExplicitZero() {
        var historical = OrganoMineralFertilizerModel.builder().build().toDto();
        assertThat(historical.getTaxaMineralizacaoPrimeiroAnoPercentual()).isNull();
        assertThat(historical.getTaxaMineralizacaoQuartoAnoPercentual()).isNull();

        var current = OrganoMineralFertilizerModel.builder()
                .taxaMineralizacaoPrimeiroAnoPercentual(40.5d)
                .taxaMineralizacaoSegundoAnoPercentual(25.25d)
                .taxaMineralizacaoTerceiroAnoPercentual(10d)
                .taxaMineralizacaoQuartoAnoPercentual(0d).build().toDto();
        assertThat(current.getTaxaMineralizacaoPrimeiroAnoPercentual()).isEqualTo(40.5d);
        assertThat(current.getTaxaMineralizacaoSegundoAnoPercentual()).isEqualTo(25.25d);
        assertThat(current.getTaxaMineralizacaoTerceiroAnoPercentual()).isEqualTo(10d);
        assertThat(current.getTaxaMineralizacaoQuartoAnoPercentual()).isZero();
    }
}
