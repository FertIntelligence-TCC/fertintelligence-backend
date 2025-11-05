package com.migueltcc.fertintelligence.model.fertintelligence.cropModels;

import com.migueltcc.fertintelligence.composedAttributes.crop.Date;
import com.migueltcc.fertintelligence.composedAttributes.foliarAnalysis.BeneficialElementsContent;
import com.migueltcc.fertintelligence.composedAttributes.foliarAnalysis.MacronutrientsContent;
import com.migueltcc.fertintelligence.composedAttributes.foliarAnalysis.MicronutrientsContent;
import com.migueltcc.fertintelligence.dto.foliarAnalysis.BeneficialElementsContentDto;
import com.migueltcc.fertintelligence.dto.foliarAnalysis.FoliarAnalysisResponseDto;
import com.migueltcc.fertintelligence.dto.foliarAnalysis.MacronutrientsContentDto;
import com.migueltcc.fertintelligence.dto.foliarAnalysis.MicronutrientsContentDto;
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

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "b_content", column = @Column(name = "TEOR_BORO", nullable = true)),
            @AttributeOverride(name = "cu_content", column = @Column(name = "TEOR_COBRE", nullable = true)),
            @AttributeOverride(name = "fe_content", column = @Column(name = "TEOR_FERRO", nullable = true)),
            @AttributeOverride(name = "ni_content", column = @Column(name = "TEOR_NIQUEL", nullable = true)),
            @AttributeOverride(name = "mn_content", column = @Column(name = "TEOR_MANGANES", nullable = true)),
            @AttributeOverride(name = "mo_content", column = @Column(name = "TEOR_MOLIBDENIO", nullable = true)),
            @AttributeOverride(name = "zn_content", column = @Column(name = "TEOR_ZINCO", nullable = true))
    })
    MicronutrientsContent micronutrients;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "n_content", column = @Column(name = "TEOR_NITROGENIO", nullable = true)),
            @AttributeOverride(name = "p_content", column = @Column(name = "TEOR_FOSFORO", nullable = true)),
            @AttributeOverride(name = "k_content", column = @Column(name = "TEOR_POTASSIO", nullable = true)),
            @AttributeOverride(name = "ca_content", column = @Column(name = "TEOR_CALCIO", nullable = true)),
            @AttributeOverride(name = "mg_content", column = @Column(name = "TEOR_MAGNESIO", nullable = true)),
            @AttributeOverride(name = "s_content", column = @Column(name = "TEOR_ENXOFRE", nullable = true))
    })
    MacronutrientsContent macronutrients;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "na_content", column = @Column(name = "TEOR_SODIO", nullable = true)),
            @AttributeOverride(name = "si_content", column = @Column(name = "TEOR_SILICIO", nullable = true)),
            @AttributeOverride(name = "v_content", column = @Column(name = "TEOR_VANADIO", nullable = true)),
            @AttributeOverride(name = "co_content", column = @Column(name = "TEOR_COBALTO", nullable = true)),
            @AttributeOverride(name = "se_content", column = @Column(name = "TEOR_SELENIO", nullable = true))
    })
    BeneficialElementsContent elements;

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
                .micronutrients(copyMicronutrients(this.micronutrients))
                .macronutrients(copyMacronutrients(this.macronutrients))
                .elements(copyBeneficialElements(this.elements))
                .build();
    }

    private Date copyDate(Date date) {
        if (date == null) {
            return null;
        }
        return new Date(date.getDay(), date.getMonth(), date.getYear());
    }

    private MicronutrientsContentDto copyMicronutrients(MicronutrientsContent micro) {
        if (micro == null) {
            return null;
        }
        return new MicronutrientsContentDto(
                micro.getB_content(),
                micro.getCu_content(),
                micro.getFe_content(),
                micro.getNi_content(),
                micro.getMn_content(),
                micro.getMo_content(),
                micro.getZn_content()
        );
    }

    private MacronutrientsContentDto copyMacronutrients(MacronutrientsContent macro) {
        if (macro == null) {
            return null;
        }
        return new MacronutrientsContentDto(
                macro.getN_content(),
                macro.getP_content(),
                macro.getK_content(),
                macro.getCa_content(),
                macro.getMg_content(),
                macro.getS_content()
        );
    }

    private BeneficialElementsContentDto copyBeneficialElements(BeneficialElementsContent elements) {
        if (elements == null) {
            return null;
        }
        return new BeneficialElementsContentDto(
                elements.getNa_content(),
                elements.getSi_content(),
                elements.getV_content(),
                elements.getCo_content(),
                elements.getSe_content()
        );
    }
}