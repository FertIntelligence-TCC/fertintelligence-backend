package com.migueltcc.fertintelligence.composedAttributes.fertilizers;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NPKrelation {

    @Column(name = "N", nullable = false)
    private double n;

    @Column(name = "P2O5", nullable = false)
    private double p;

    @Column(name = "K2O", nullable = false)
    private double k;
}
