package com.migueltcc.fertintelligence.model.fertintelligence.cropModels.foliarFertilizationModels;

import com.migueltcc.fertintelligence.composedAttributes.crop.Date;
import com.migueltcc.fertintelligence.composedAttributes.foliarAnalysis.AppliedMicronutrient;
import com.migueltcc.fertintelligence.dto.foliarFertilization.liquid.LiquidSourceResponseDto; // Import adicionado
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.CropModel;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
@Table(name = "FONTE_LIQUIDA")
@EqualsAndHashCode
public class LiquidSourceModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "CULTURA", nullable = false)
    CropModel crop;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "day", column = @Column(name = "DATA_DIA", nullable = false)),
            @AttributeOverride(name = "month", column = @Column(name = "DATA_MES", nullable = false)),
            @AttributeOverride(name = "year", column = @Column(name = "DATA_ANO", nullable = false))
    })
    Date date;

    @Column(name = "MICRONUTRIENTE_APLICADO", nullable = false)
    @Enumerated(EnumType.STRING)
    AppliedMicronutrient micronutrient;

    @Column(name = "FONTE", nullable = false)
    String source;

    @Column(name = "CONCENTRACAO", nullable = false)
    Double concentration;

    @Column(name = "DENSIDADE", nullable = false)
    Double density;

    @Column(name = "VOLUME_APLICADO", nullable = false)
    Double applied_volume;

    @Column(name = "VOLUME_CALDA", nullable = false)
    Double tail_volume;

    public LiquidSourceResponseDto toDto() {
        return LiquidSourceResponseDto.builder()
                .id(this.id)
                .crop_id(this.crop.getId())
                .date(copyDate(this.date))
                .micronutrient(this.micronutrient)
                .source(this.source)
                .concentration(this.concentration)
                .density(this.density)
                .applied_volume(this.applied_volume)
                .tail_volume(this.tail_volume)
                .build();
    }

    private Date copyDate(Date date) {
        if (date == null) {
            return null;
        }
        return new Date(date.getDay(), date.getMonth(), date.getYear());
    }
}