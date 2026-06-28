package com.migueltcc.fertintelligence.composedAttributes.crop;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum CropSpacingMode {
    PLANTS_PER_LINEAR_METER,
    PIT,
    UNKNOWN;

    @JsonCreator
    public static CropSpacingMode fromJson(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String normalized = value.trim().toUpperCase()
                .replace("-", "_")
                .replace(" ", "_");

        if ("LINEAR_METER".equals(normalized)) {
            return PLANTS_PER_LINEAR_METER;
        }

        return CropSpacingMode.valueOf(normalized);
    }
}
