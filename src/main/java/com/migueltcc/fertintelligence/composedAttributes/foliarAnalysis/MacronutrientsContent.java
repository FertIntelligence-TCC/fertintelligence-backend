package com.migueltcc.fertintelligence.composedAttributes.foliarAnalysis;

import jakarta.persistence.*;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MacronutrientsContent {

    @Column(name = "TEOR_NITROGENIO", nullable = true)
    Double n_content;

    @Column(name = "TEOR_FOSFORO", nullable = true)
    Double p_content;

    @Column(name = "TEOR_POTASSIO", nullable = true)
    Double k_content;

    @Column(name = "TEOR_CALCIO", nullable = true)
    Double ca_content;

    @Column(name = "TEOR_MAGNESIO", nullable = true)
    Double mg_content;

    @Column(name = "TEOR_ENXOFRE", nullable = true)
    Double s_content;

}
