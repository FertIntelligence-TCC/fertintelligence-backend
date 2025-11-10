package com.migueltcc.fertintelligence.composedAttributes.crop;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Date {

    @Column(name = "DIA", nullable = false)
    private int day;

    @Column(name = "MES", nullable = false)
    private int month;

    @Column(name = "ANO", nullable = false)
    private int year;
}
