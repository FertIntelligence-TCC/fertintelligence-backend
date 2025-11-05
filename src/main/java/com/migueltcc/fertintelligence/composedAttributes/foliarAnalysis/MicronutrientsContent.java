package com.migueltcc.fertintelligence.composedAttributes.foliarAnalysis;

import jakarta.persistence.*;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MicronutrientsContent {

    @Column(name = "TEOR_BORO", nullable = true)
    Double b_content;

    @Column(name = "TEOR_COBRE", nullable = true)
    Double cu_content;

    @Column(name = "TEOR_FERRO", nullable = true)
    Double fe_content;

    @Column(name = "TEOR_NIQUEL", nullable = true)
    Double ni_content;

    @Column(name = "TEOR_MANGANES", nullable = true)
    Double mn_content;

    @Column(name = "TEOR_MOLIBDENIO", nullable = true)
    Double mo_content;

    @Column(name = "TEOR_ZINCO", nullable = true)
    Double zn_content;

}
