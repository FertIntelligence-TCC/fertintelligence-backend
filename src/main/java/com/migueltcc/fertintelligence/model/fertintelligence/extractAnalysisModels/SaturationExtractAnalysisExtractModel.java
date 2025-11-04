package com.migueltcc.fertintelligence.model.fertintelligence.extractAnalysisModels;

import com.migueltcc.fertintelligence.dto.extractAnalysis.saturationExtract.SaturationExtractAnalysisExtractResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.extractModels.LayerExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractModels.RangeExtractModel;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
@Table(name = "EXTRATOS_ANALISES_EXTRATO_SATURACAO")
@EqualsAndHashCode
public class SaturationExtractAnalysisExtractModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne
    @JoinColumn(name = "ID_EXTRATO_INTERVALOS", nullable = true) // rangeExtract ou layerExtract deve ser nulo!
    RangeExtractModel rangeExtract;

    @OneToOne
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

    // Dureza em CaCO3, mg/dm³
    @Column(name = "DUREZA_CACO3", nullable = true)
    Double durezaCaCO3;

    // Dureza total em CaCO3, mg/dm³
    @Column(name = "DUREZA_TOTAL_CACO3", nullable = true)
    Double durezaTotalCaCO3;

    // RAS, em mmolc/mmolc**(1/2)
    @Column(name = "RAS", nullable = true)
    Double ras;

    // PST, %
    @Column(name = "PST", nullable = true)
    Double pst;

    public SaturationExtractAnalysisExtractResponseDto toDto() {
        RangeExtractModel range = this.rangeExtract;
        LayerExtractModel layer = this.layerExtract;

        Long rangeExtractId = null;
        Long layerExtractId = null;
        Integer initialDepth = null;
        Integer finalDepth = null;
        com.migueltcc.fertintelligence.composedAttributes.SoilExtracts.Camada camada = null;
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
                .ph(this.ph)
                .ce(this.ce)
                .teorCO3(this.teorCO3)
                .teorHCO3(this.teorHCO3)
                .teorNO3(this.teorNO3)
                .teorH2PO4(this.teorH2PO4)
                .teorSO4(this.teorSO4)
                .teorNa(this.teorNa)
                .teorK(this.teorK)
                .teorCa(this.teorCa)
                .teorMg(this.teorMg)
                .residuosSuspensao(this.residuosSuspensao)
                .durezaCaCO3(this.durezaCaCO3)
                .durezaTotalCaCO3(this.durezaTotalCaCO3)
                .ras(this.ras)
                .pst(this.pst)
                .build();
    }

}