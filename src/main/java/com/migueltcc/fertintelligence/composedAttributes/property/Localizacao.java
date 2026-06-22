// Local: /com/migueltcc/fertintelligence/composedAtributes/Localizacao.java
package com.migueltcc.fertintelligence.composedAttributes.property;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Localizacao {

    @Column(name = "LATITUDE", nullable = false)
    private Double latitude;

    @Column(name = "LATITUDE_GRAUS")
    private Integer latitudeGraus;

    @Column(name = "LATITUDE_MINUTOS")
    private Integer latitudeMinutos;

    @Column(name = "LATITUDE_SEGUNDOS")
    private Double latitudeSegundos;

    @Column(name = "NORTE/SUL", nullable = false)
    private LatitudeDirection latDirection;

    @Column(name = "LONGITUDE", nullable = false)
    private Double longitude;

    @Column(name = "LONGITUDE_GRAUS")
    private Integer longitudeGraus;

    @Column(name = "LONGITUDE_MINUTOS")
    private Integer longitudeMinutos;

    @Column(name = "LONGITUDE_SEGUNDOS")
    private Double longitudeSegundos;

    @Column(name = "OESTE/LESTE", nullable = false)
    private LongitudeDirection longDirection;

    @Column(name = "ALTITUDE", nullable = false)
    private Double altitude;

    public Localizacao(Double latitude,
                       LatitudeDirection latDirection,
                       Double longitude,
                       LongitudeDirection longDirection,
                       Double altitude) {
        this.latitude = latitude;
        this.latDirection = latDirection;
        this.longitude = longitude;
        this.longDirection = longDirection;
        this.altitude = altitude;
    }
}
