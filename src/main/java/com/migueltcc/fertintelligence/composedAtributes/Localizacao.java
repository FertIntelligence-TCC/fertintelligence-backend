// Local: /com/migueltcc/fertintelligence/composedAtributes/Localizacao.java
package com.migueltcc.fertintelligence.composedAtributes;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable // Indica que esta classe será embutida em outra entidade
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Localizacao {

    @Column(name = "LATITUDE", nullable = false)
    private Double latitude;

    @Column(name = "LONGITUDE", nullable = false)
    private Double longitude;

    @Column(name = "ALTITUDE", nullable = false)
    private Double altitude;
}