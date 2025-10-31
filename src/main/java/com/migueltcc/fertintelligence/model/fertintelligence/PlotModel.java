package com.migueltcc.fertintelligence.model.fertintelligence;

import com.migueltcc.fertintelligence.composedAttributes.Plot.AreaIrrigada;
import com.migueltcc.fertintelligence.composedAttributes.Plot.ClasseSolo;
import com.migueltcc.fertintelligence.composedAttributes.Plot.TexturaSolo;
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
                .propertyId(this.property.getId())
                .propertyName(this.property.getNome())
                .build();
    }
}