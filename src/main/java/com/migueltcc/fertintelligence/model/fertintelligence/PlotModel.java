package com.migueltcc.fertintelligence.model.fertintelligence;

import com.migueltcc.fertintelligence.composedAttributes.plot.AreaIrrigada;
import com.migueltcc.fertintelligence.composedAttributes.plot.ClasseSolo;
import com.migueltcc.fertintelligence.composedAttributes.plot.TexturaSolo;
import com.migueltcc.fertintelligence.composedAttributes.property.LatitudeDirection;
import com.migueltcc.fertintelligence.composedAttributes.property.LongitudeDirection;
import com.migueltcc.fertintelligence.dto.plot.PlotResponseDto;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
@Table(name = "TALHOES")
@EqualsAndHashCode
public class PlotModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "ID_PROPRIEDADE", nullable = false)
    PropertyModel property;

    @Column(name = "IDENTIFICACAO", nullable = false, length = 100)
    String identification;

    @Column(name = "AREA", nullable = false)
    Double area;

    @Column(name = "CLASSE_SOLO", nullable = false)
    ClasseSolo soilClass;

    @Column(name = "TEXTURA_SOLO", nullable = false)
    TexturaSolo soilTexture;

    @Column(name = "ANO_INCORPORACAO_SAFRA", nullable = false)
    Integer cropIncorporationYear;

    @Column(name = "AREA_IRRIGADA", nullable = false)
    AreaIrrigada irrigatedArea;

    @Column(name = "DECLIVIDADE", nullable = false)
    Double declivity;

    @Column(name = "PLUVIOSIDADE_MENSAL", nullable = false)
    Double monthlyPluviosity;

    @Column(name = "PLUVIOSIDADE_ANUAL", nullable = false)
    Double annualPluviosity;

    @Column(name = "LATITUDE")
    Double latitude;

    @Column(name = "LATITUDE_GRAUS")
    Integer latitudeGraus;

    @Column(name = "LATITUDE_MINUTOS")
    Integer latitudeMinutos;

    @Column(name = "LATITUDE_SEGUNDOS")
    Double latitudeSegundos;

    @Column(name = "NORTE/SUL")
    LatitudeDirection latitudeDirection;

    @Column(name = "LONGITUDE")
    Double longitude;

    @Column(name = "LONGITUDE_GRAUS")
    Integer longitudeGraus;

    @Column(name = "LONGITUDE_MINUTOS")
    Integer longitudeMinutos;

    @Column(name = "LONGITUDE_SEGUNDOS")
    Double longitudeSegundos;

    @Column(name = "OESTE/LESTE")
    LongitudeDirection longitudeDirection;

    @Column(name = "ALTITUDE")
    Double altitude;

    @Column(name = "ID_FOTO")
    String idFoto;

    public PlotResponseDto toDto() {
        return PlotResponseDto.builder()
                .id(this.id)
                .identification(this.identification)
                .area(this.area)
                .soilClass(this.soilClass)
                .soilTexture(this.soilTexture)
                .cropIncorporationYear(this.cropIncorporationYear)
                .irrigatedArea(this.irrigatedArea)
                .declivity(this.declivity)
                .monthlyPluviosity(this.monthlyPluviosity)
                .annualPluviosity(this.annualPluviosity)
                .latitude(this.latitude)
                .latitudeGraus(this.latitudeGraus)
                .latitudeMinutos(this.latitudeMinutos)
                .latitudeSegundos(this.latitudeSegundos)
                .latitudeDirection(this.latitudeDirection)
                .longitude(this.longitude)
                .longitudeGraus(this.longitudeGraus)
                .longitudeMinutos(this.longitudeMinutos)
                .longitudeSegundos(this.longitudeSegundos)
                .longitudeDirection(this.longitudeDirection)
                .altitude(this.altitude)
                .idFoto(this.idFoto)
                .propertyId(this.property.getId())
                .propertyName(this.property.getNome())
                .build();
    }
}
