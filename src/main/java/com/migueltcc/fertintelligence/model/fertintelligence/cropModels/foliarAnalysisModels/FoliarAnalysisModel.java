package com.migueltcc.fertintelligence.model.fertintelligence.cropModels.foliarAnalysisModels;

import com.migueltcc.fertintelligence.composedAttributes.crop.Date;
import com.migueltcc.fertintelligence.dto.foliarAnalysis.FoliarAnalysisResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.cropModels.CropModel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
@Table(name = "ANALISE_FOLIAR")
@EqualsAndHashCode
public class FoliarAnalysisModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "day", column = @Column(name = "DATA_COLETA_DIA", nullable = false)),
            @AttributeOverride(name = "month", column = @Column(name = "DATA_COLETA_MES", nullable = false)),
            @AttributeOverride(name = "year", column = @Column(name = "DATA_COLETA_ANO", nullable = false))
    })
    Date collectDate;

    @Column(name = "LABORATORY", nullable = false)
    String laboratory;

    @OneToOne
    @JoinColumn(name = "ID_CULTURA", nullable = false)
    CropModel crop;

    public FoliarAnalysisResponseDto toDto() {
        return FoliarAnalysisResponseDto.builder()
                .id(this.id)
                .collectDate(copyDate(this.collectDate))
                .laboratory(this.laboratory)
                .cropId(this.crop.getId())
                .cropName(this.crop.getName())
                .cropVariety(this.crop.getVariety())
                .build();
    }

    private Date copyDate(Date date) {
        if (date == null) {
            return null;
        }
        return new Date(date.getDay(), date.getMonth(), date.getYear());
    }
}