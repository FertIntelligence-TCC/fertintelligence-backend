package com.migueltcc.fertintelligence.model.fertintelligence.extractAnalysisModels;

import com.migueltcc.fertintelligence.composedAttributes.saturationExtract.SaturationExtractUnit;
import com.migueltcc.fertintelligence.composedAttributes.saturationExtract.SaturationExtractUnitConverter;
import com.migueltcc.fertintelligence.dto.extractAnalysis.saturationExtract.SaturationExtractAnalysisExtractResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.extractModels.LayerExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractModels.RangeExtractModel;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
@Table(name = "EXTRATOS_ANALISES_EXTRATO_SATURACAO")
@EqualsAndHashCode
public class SaturationExtractAnalysisExtractModel {

    private static final SaturationExtractUnit DEFAULT_RAS_UNIT = SaturationExtractUnit.MMOLC_POWER_HALF;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_EXTRATO_INTERVALOS", nullable = true) // rangeExtract ou layerExtract deve ser nulo!
    RangeExtractModel rangeExtract;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_EXTRATO_CAMADAS", nullable = true) // rangeExtract ou layerExtract deve ser nulo!
    LayerExtractModel layerExtract;

    // pH
    @Column(name = "PH", nullable = true)
    Double ph;

    // CE, dS/m
    @Column(name = "CE", nullable = true)
    Double ce;

    // Teor de CO3=, mg/dm³
    @Column(name = "TEOR_CO3", nullable = true)
    Double teorCO3;

    // Teor de HCO3-, mg/dm³
    @Column(name = "TEOR_HCO3", nullable = true)
    Double teorHCO3;

    // Teor de NO3-, mg/dm³
    @Column(name = "TEOR_NO3", nullable = true)
    Double teorNO3;

    // Teor de H2PO4-, mg/dm³
    @Column(name = "TEOR_H2PO4", nullable = true)
    Double teorH2PO4;

    // Teor de SO4=, mg/dm³
    @Column(name = "TEOR_SO4", nullable = true)
    Double teorSO4;

    // Teor de Cl-, mg/dm³
    @Column(name = "TEOR_CL", nullable = true)
    Double teorCl;

    // Teor de Na+, mg/dm³
    @Column(name = "TEOR_NA", nullable = true)
    Double teorNa;

    // Teor de K+, mg/dm³
    @Column(name = "TEOR_K", nullable = true)
    Double teorK;

    // Teor de Ca2+, mg/dm³
    @Column(name = "TEOR_CA", nullable = true)
    Double teorCa;

    // Teor de Mg2+, mg/dm³
    @Column(name = "TEOR_MG", nullable = true)
    Double teorMg;

    // Resíduos em suspensão, mg/dm³
    @Column(name = "RESIDUOS_SUSPENSAO", nullable = true)
    Double residuosSuspensao;

    // Campo legado mantido para compatibilidade com bancos existentes; não faz parte do contrato novo.
    @Column(name = "DUREZA_CACO3", nullable = true)
    Double durezaCaCO3;

    // Dureza total em CaCO3, mg/dm³
    @Column(name = "DUREZA_TOTAL_CACO3", nullable = true)
    Double durezaTotalCaCO3;

    // RAS
    @Column(name = "RAS", nullable = true)
    Double ras;

    @Convert(converter = SaturationExtractUnitConverter.class)
    @Column(name = "UNIDADE_RAS", nullable = false)
    @Builder.Default
    SaturationExtractUnit unidadeRas = DEFAULT_RAS_UNIT;

    @PrePersist
    @PreUpdate
    public void normalizeSaturationUnits() {
        this.unidadeRas = normalizeSaturationUnit(this.unidadeRas);
    }

    public SaturationExtractUnit normalizeSaturationUnit(SaturationExtractUnit unit) {
        return unit != null ? unit.canonicalForSaturationExtract() : DEFAULT_RAS_UNIT;
    }

    public SaturationExtractAnalysisExtractResponseDto toDto() {
        normalizeSaturationUnits();

        RangeExtractModel range = this.rangeExtract;
        LayerExtractModel layer = this.layerExtract;

        Long rangeExtractId = null;
        Long layerExtractId = null;
        Integer initialDepth = null;
        Integer finalDepth = null;
        com.migueltcc.fertintelligence.composedAttributes.soilExtracts.Camada camada = null;
        Integer subLayer = null;

        if (range != null) {
            rangeExtractId = range.getId();
            initialDepth = range.getProfundidade_inicial();
            finalDepth = range.getProfundidade_final();
        } else if (layer != null) {
            layerExtractId = layer.getId();
            initialDepth = layer.getProfundidade_inicial();
            finalDepth = layer.getProfundidade_final();
            camada = layer.getLayer();
            subLayer = layer.getSub_layer();
        }

        return SaturationExtractAnalysisExtractResponseDto.builder()
                .id(this.id)
                .rangeExtractId(rangeExtractId)
                .layerExtractId(layerExtractId)
                .initialDepth(initialDepth)
                .finalDepth(finalDepth)
                .layer(camada)
                .subLayer(subLayer)
                .ph(round2(this.ph))
                .ce(round2(this.ce))
                .teorCO3(round2(this.teorCO3))
                .teorHCO3(round2(this.teorHCO3))
                .teorNO3(round2(this.teorNO3))
                .teorH2PO4(round2(this.teorH2PO4))
                .teorSO4(round2(this.teorSO4))
                .teorCl(round2(this.teorCl))
                .teorNa(round2(this.teorNa))
                .teorK(round2(this.teorK))
                .teorCa(round2(this.teorCa))
                .teorMg(round2(this.teorMg))
                .residuosSuspensao(round2(this.residuosSuspensao))
                .ras(round2(this.ras))
                .unidadeRas(normalizeSaturationUnit(this.unidadeRas))
                .build();
    }

    private Double round2(Double value) {
        if (value == null) {
            return null;
        }
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

}
