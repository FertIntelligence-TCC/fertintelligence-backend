package com.migueltcc.fertintelligence.model.fertintelligence;

import com.migueltcc.fertintelligence.composedAttributes.Crop.CultivationType;
import com.migueltcc.fertintelligence.composedAttributes.Crop.Date;
import com.migueltcc.fertintelligence.dto.crop.CropResponseDto;
import jakarta.persistence.*;

public class CropModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "ID_PASTA_PROPRIEDADES_ANUAL", nullable = false)
    AnnualCropFolderModel folder;

    @Column(name = "TIPO_CULTIVO", nullable = false)
    CultivationType cultivationType;

    @Column(name = "NOME", nullable = false)
    String name;

    @Column(name = "VARIEDADE", nullable = false)
    String variety;

    @Column(name = "CICLO", nullable = false)
    Integer cycle;

    @Column(name = "DISTANCIA_ENTRE_LINHAS", nullable = false)
    Double distanceBetweenLines;

    @Column(name = "N_PLANTAS_POR_METRO", nullable = false)
    Double plantsPerMeter;

    @Column(name = "PRODUTIVIDADE_ESPERADA", nullable = false)
    Double expectedProductivity;

    @Column(name = "PRODUTIVIDADE_OBTIDA", nullable = false)
    Double obtainedProductivity;

    @Column(name = "AREA_USADA_NO_TALHAO", nullable = false)
    Double usedAreaInThePlot;

    @Column(name = "DATA_PLANTIO", nullable = false)
    Date plantingDate;

    @Column(name = "DATA_EMERGENCIA", nullable = false)
    Date emergenceDate;

    @Column(name = "DATA_ABOTOAMENTO", nullable = true)
    Date buttoningDate;

    @Column(name = "DATA_FLORESCIMENTO", nullable = false)
    Date floweringDate;

    @Column(name = "DATA_COLHEITA", nullable = false)
    Date harvestDate;

    public CropResponseDto toDto() {
        return CropResponseDto.builder()
                .id(this.id)
                .cultivationType(this.cultivationType)
                .name(this.name)
                .variety(this.variety)
                .cycle(this.cycle)
                .distanceBetweenLines(this.distanceBetweenLines)
                .plantsPerMeter(this.plantsPerMeter)
                .expectedProductivity(this.expectedProductivity)
                .obtainedProductivity(this.obtainedProductivity)
                .usedAreaInThePlot(this.usedAreaInThePlot)
                .plantingDate(new Date(
                        this.plantingDate.getDay(),
                        this.plantingDate.getMonth(),
                        this.plantingDate.getYear()
                ))
                .emergenceDate(new Date(
                        this.emergenceDate.getDay(),
                        this.emergenceDate.getMonth(),
                        this.emergenceDate.getYear()
                ))
                .buttoningDate(new Date(
                        this.buttoningDate.getDay(),
                        this.buttoningDate.getMonth(),
                        this.buttoningDate.getYear()
                ))
                .floweringDate(new Date(
                        this.floweringDate.getDay(),
                        this.floweringDate.getMonth(),
                        this.floweringDate.getYear()
                ))
                .harvestDate(new Date(
                        this.harvestDate.getDay(),
                        this.harvestDate.getMonth(),
                        this.harvestDate.getYear()
                ))
                .folder_id(this.folder.getId())
                .cropsYear(this.folder.getCropsYear())
                .build();
    }

}
