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
public class Formulate {

    @Column(name = "N", nullable = false)
    private int n;

    @Column(name = "P2O5", nullable = false)
    private int p;

    @Column(name = "K2O", nullable = false)
    private int k;
}
