// Local: /com/migueltcc/fertintelligence/composedAtributes/Localizacao.java
package com.migueltcc.fertintelligence.composedAtributes;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Localizacao {

    @Column(name = "LATITUDE", nullable = false)
    private Double latitude;

    @Column(name = "NORTE/SUL", nullable = false)
    private LatitudeDirection latDirection;

    @Column(name = "LONGITUDE", nullable = false)
    private Double longitude;

    @Column(name = "OESTE/LESTE", nullable = false)
    private LongitudeDirection longDirection;

    @Column(name = "ALTITUDE", nullable = false)
    private Double altitude;
}