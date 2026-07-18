package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.crop.Date;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CropPhenologyDateValidatorTest {

    @Test
    void acceptsEqualDatesAndAChronologicalSequence() {
        assertThatCode(() -> CropPhenologyDateValidator.validate(
                date(1, 8, 2026), date(1, 8, 2026), date(20, 8, 2026),
                date(10, 9, 2026), date(19, 12, 2026)))
                .doesNotThrowAnyException();
    }

    @Test
    void validatesEveryRequiredTransition() {
        assertInvalid(date(2, 8, 2026), date(1, 8, 2026), date(20, 8, 2026), date(10, 9, 2026), date(19, 12, 2026), "emergência");
        assertInvalid(date(1, 8, 2026), date(2, 8, 2026), date(1, 8, 2026), date(10, 9, 2026), date(19, 12, 2026), "abotoamento");
        assertInvalid(date(1, 8, 2026), date(2, 8, 2026), date(20, 8, 2026), date(19, 8, 2026), date(19, 12, 2026), "florescimento");
        assertInvalid(date(1, 8, 2026), date(2, 8, 2026), date(20, 8, 2026), date(10, 9, 2026), date(9, 9, 2026), "colheita");
    }

    @Test
    void validatesFloweringAgainstEmergenceWhenButtoningIsAbsent() {
        assertInvalid(date(1, 8, 2026), date(10, 8, 2026), null,
                date(9, 8, 2026), date(19, 12, 2026), "florescimento");
    }

    @Test
    void rejectsInvalidCalendarDate() {
        assertThatThrownBy(() -> CropPhenologyDateValidator.validate(
                date(31, 2, 2026), date(1, 3, 2026), null, date(2, 3, 2026), date(3, 3, 2026)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("plantio");
    }

    private void assertInvalid(Date planting, Date emergence, Date buttoning, Date flowering, Date harvest, String message) {
        assertThatThrownBy(() -> CropPhenologyDateValidator.validate(planting, emergence, buttoning, flowering, harvest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(message);
    }

    private Date date(int day, int month, int year) {
        return new Date(day, month, year);
    }
}
