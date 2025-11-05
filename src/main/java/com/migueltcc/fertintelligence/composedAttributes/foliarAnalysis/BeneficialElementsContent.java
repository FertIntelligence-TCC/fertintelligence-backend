package com.migueltcc.fertintelligence.composedAttributes.foliarAnalysis;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BeneficialElementsContent {

    @Column(name = "TEOR_SODIO", nullable = true)
    Double na_content;

    @Column(name = "TEOR_SILICIO", nullable = true)
    Double si_content;

    @Column(name = "TEOR_VANADIO", nullable = true)
    Double v_content;

    @Column(name = "TEOR_COBALTO", nullable = true)
    Double co_content;

    @Column(name = "TEOR_SELENIO", nullable = true)
    Double se_content;

}
