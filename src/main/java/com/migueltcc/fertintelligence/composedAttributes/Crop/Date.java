package com.migueltcc.fertintelligence.composedAttributes.Crop;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Date {
    private int day;
    private int month;
    private int year;
}
