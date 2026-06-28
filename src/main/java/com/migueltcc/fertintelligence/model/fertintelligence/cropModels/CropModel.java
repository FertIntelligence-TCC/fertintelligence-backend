package com.migueltcc.fertintelligence.model.fertintelligence.cropModels;

import com.migueltcc.fertintelligence.composedAttributes.crop.CultivationType;
import com.migueltcc.fertintelligence.composedAttributes.crop.CropSpacingMode;
import com.migueltcc.fertintelligence.composedAttributes.crop.Date;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.NomeComum;
import com.migueltcc.fertintelligence.dto.crop.CropResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.AnnualCropFolderModel;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
@Table(name = "CULTURAS")
@EqualsAndHashCode
public class CropModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "ID_PASTA_PROPRIEDADES_ANUAL", nullable = false)
    AnnualCropFolderModel folder;

    @Column(name = "TIPO_CULTIVO", nullable = false)
    @Enumerated(EnumType.STRING)
    CultivationType cultivationType;

    @Column(name = "NOME", nullable = false)
    @Enumerated(EnumType.STRING)
    NomeComum name;

    @Column(name = "VARIEDADE", nullable = false)
    String variety;

    @Column(name = "CICLO", nullable = false)
    Integer cycle;

    @Column(name = "DISTANCIA_ENTRE_LINHAS", nullable = false)
    Double distanceBetweenLines;

    @Column(name = "N_PLANTAS_POR_METRO", nullable = false)
    Double plantsPerMeter;

    @Builder.Default
    @Column(name = "MODO_ESPACAMENTO")
    @Enumerated(EnumType.STRING)
    CropSpacingMode spacingMode = CropSpacingMode.PLANTS_PER_LINEAR_METER;

    @Column(name = "DISTANCIA_ENTRE_COVAS")
    Double distanceBetweenPits;

    @Column(name = "N_PLANTAS_POR_COVA")
    Double plantsPerPit;

    @Column(name = "PRODUTIVIDADE_ESPERADA", nullable = false)
    Double expectedProductivity;

    @Column(name = "PRODUTIVIDADE_OBTIDA", nullable = false)
    Double obtainedProductivity;

    @Column(name = "AREA_USADA_NO_TALHAO", nullable = false)
    Double usedAreaInThePlot;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "day", column = @Column(name = "DATA_PLANTIO_DIA", nullable = false)),
            @AttributeOverride(name = "month", column = @Column(name = "DATA_PLANTIO_MES", nullable = false)),
            @AttributeOverride(name = "year", column = @Column(name = "DATA_PLANTIO_ANO", nullable = false))
    })
    Date plantingDate;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "day", column = @Column(name = "DATA_EMERGENCIA_DIA", nullable = false)),
            @AttributeOverride(name = "month", column = @Column(name = "DATA_EMERGENCIA_MES", nullable = false)),
            @AttributeOverride(name = "year", column = @Column(name = "DATA_EMERGENCIA_ANO", nullable = false))
    })
    Date emergenceDate;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "day", column = @Column(name = "DATA_ABOTOAMENTO_DIA")),
            @AttributeOverride(name = "month", column = @Column(name = "DATA_ABOTOAMENTO_MES")),
            @AttributeOverride(name = "year", column = @Column(name = "DATA_ABOTOAMENTO_ANO"))
    })
    Date buttoningDate;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "day", column = @Column(name = "DATA_FLORESCIMENTO_DIA", nullable = false)),
            @AttributeOverride(name = "month", column = @Column(name = "DATA_FLORESCIMENTO_MES", nullable = false)),
            @AttributeOverride(name = "year", column = @Column(name = "DATA_FLORESCIMENTO_ANO", nullable = false))
    })
    Date floweringDate;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "day", column = @Column(name = "DATA_COLHEITA_DIA", nullable = false)),
            @AttributeOverride(name = "month", column = @Column(name = "DATA_COLHEITA_MES", nullable = false)),
            @AttributeOverride(name = "year", column = @Column(name = "DATA_COLHEITA_ANO", nullable = false))
    })
    Date harvestDate;

    @Column(name = "ID_FOTO")
    String idFoto;

    public CropResponseDto toDto() {
        return CropResponseDto.builder()
                .id(this.id)
                .cultivationType(this.cultivationType)
                .name(this.name)
                .variety(this.variety)
                .cycle(this.cycle)
                .distanceBetweenLines(this.distanceBetweenLines)
                .plantsPerMeter(this.plantsPerMeter)
                .spacingMode(this.spacingMode)
                .distanceBetweenPits(this.distanceBetweenPits)
                .plantsPerPit(this.plantsPerPit)
                .expectedProductivity(this.expectedProductivity)
                .obtainedProductivity(this.obtainedProductivity)
                .usedAreaInThePlot(this.usedAreaInThePlot)
                .plantingDate(copyDate(this.plantingDate))
                .emergenceDate(copyDate(this.emergenceDate))
                .buttoningDate(copyDate(this.buttoningDate))
                .floweringDate(copyDate(this.floweringDate))
                .harvestDate(copyDate(this.harvestDate))
                .idFoto(this.idFoto)
                .folder_id(this.folder.getId())
                .cropsYear(this.folder.getCropsYear())
                .build();
    }

    private Date copyDate(Date date) {
        if (date == null) {
            return null;
        }
        return new Date(date.getDay(), date.getMonth(), date.getYear());
    }
}
