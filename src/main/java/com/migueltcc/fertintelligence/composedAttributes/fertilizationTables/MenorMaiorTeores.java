package com.migueltcc.fertintelligence.composedAttributes.fertilizationTables;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Builder
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MenorMaiorTeores {

    @Column(name = "MENOR_TEOR", nullable = true)
    Double menor;

    @Column(name = "MAIOR_TEOR", nullable = true)
    Double maior;

    @Column(name = "UNIDADE_TEORES", nullable = false)
    private UnidadeTeor unity;

}
