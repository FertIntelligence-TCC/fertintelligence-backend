package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.crop.Date;

import java.time.DateTimeException;
import java.time.LocalDate;

final class CropPhenologyDateValidator {

    private CropPhenologyDateValidator() {
    }

    static void validate(Date planting, Date emergence, Date buttoning, Date flowering, Date harvest) {
        LocalDate plantingDate = required(planting, "plantio");
        LocalDate emergenceDate = required(emergence, "emergência");
        LocalDate floweringDate = required(flowering, "florescimento");
        LocalDate harvestDate = required(harvest, "colheita");
        LocalDate buttoningDate = optional(buttoning, "abotoamento");

        requireNotBefore(emergenceDate, plantingDate,
                "A data de emergência não pode ser anterior à data de plantio.");
        if (buttoningDate != null) {
            requireNotBefore(buttoningDate, emergenceDate,
                    "A data de abotoamento não pode ser anterior à data de emergência.");
            requireNotBefore(floweringDate, buttoningDate,
                    "A data de florescimento não pode ser anterior à data de abotoamento.");
        } else {
            requireNotBefore(floweringDate, emergenceDate,
                    "A data de florescimento não pode ser anterior à data de emergência.");
        }
        requireNotBefore(harvestDate, floweringDate,
                "A data de colheita não pode ser anterior à data de florescimento.");
    }

    private static LocalDate required(Date date, String label) {
        LocalDate value = optional(date, label);
        if (value == null) {
            throw new IllegalArgumentException("A data de " + label + " deve ser informada.");
        }
        return value;
    }

    private static LocalDate optional(Date date, String label) {
        if (date == null) return null;
        try {
            return LocalDate.of(date.getYear(), date.getMonth(), date.getDay());
        } catch (DateTimeException exception) {
            throw new IllegalArgumentException("A data de " + label + " é inválida.");
        }
    }

    private static void requireNotBefore(LocalDate current, LocalDate previous, String message) {
        if (current.isBefore(previous)) {
            throw new IllegalArgumentException(message);
        }
    }
}
