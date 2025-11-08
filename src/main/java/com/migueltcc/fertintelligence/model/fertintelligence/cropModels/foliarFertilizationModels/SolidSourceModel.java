package com.migueltcc.fertintelligence.model.fertintelligence.cropModels.foliarFertilizationModels;

import com.migueltcc.fertintelligence.composedAttributes.crop.Date;
import com.migueltcc.fertintelligence.composedAttributes.foliarAnalysis.AppliedMicronutrient;
import com.migueltcc.fertintelligence.dto.foliarFertilization.solidSource.SolidSourceResponseDto; // Import adicionado
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.CropModel;
import jakarta.persistence.*;
import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
@Table(name = "FONTE_SOLIDA")
@EqualsAndHashCode
public class SolidSourceModel {

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

    @Column(name = "QUANTIDADE_APLICADA", nullable = false)
    Double quantity;

    public SolidSourceResponseDto toDto() {
        return SolidSourceResponseDto.builder()
                .id(this.id)
                .crop_id(this.crop.getId())
                .date(copyDate(this.date))
                .micronutrient(this.micronutrient)
                .source(this.source)
                .concentration(this.concentration)
                .quantity(this.quantity)
                .build();
    }

    private Date copyDate(Date date) {
        if (date == null) {
            return null;
        }
        return new Date(date.getDay(), date.getMonth(), date.getYear());
    }
}