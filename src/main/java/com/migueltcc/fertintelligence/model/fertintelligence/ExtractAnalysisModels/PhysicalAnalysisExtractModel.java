package com.migueltcc.fertintelligence.model.fertintelligence.ExtractAnalysisModels;

import com.migueltcc.fertintelligence.dto.extractAnalysis.physical.PhysicalAnalysisExtractResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.ExtractModels.LayerExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.ExtractModels.RangeExtractModel;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
@Table(name = "EXTRATOS_ANALISES_FISICAS")
@EqualsAndHashCode
public class PhysicalAnalysisExtractModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne
    @JoinColumn(name = "ID_EXTRATO_INTERVALOS", nullable = true) // rangeExtract ou layerExtract deve ser nulo!
    RangeExtractModel rangeExtract;

    @OneToOne
    @JoinColumn(name = "ID_EXTRATO_CAMADAS", nullable = true) // rangeExtract ou layerExtract deve ser nulo!
    LayerExtractModel layerExtract;

    // Teor de Areia (g/dm3)
    @Column(name = "TEOR_DE_AREIA", nullable = true)
    double teorAreia;

    // Teor de Silte (g/dm3)
    @Column(name = "TEOR_DE_SILTE", nullable = true)
    double teorSilte;

    // Teor de Argila (g/dm3)
    @Column(name = "TEOR_DE_ARGILA", nullable = true)
    double teorArgila;

    // Densidade Aparente (g/cm3)
    @Column(name = "DENSIDADE_APARENTE", nullable = true)
    double densidadeAparente;

    // Densidade Real (g/cm3)
    @Column(name = "DENSIDADE_REAL", nullable = true)
    double densidadeReal;

    // Porosidade Total (%)
    @Column(name = "POROSIDADE_TOTAL", nullable = true)
    double porosidadeTotal;

    // Microporosidade (%)
    @Column(name = "MICROPOROSIDADE", nullable = true)
    double microporosidade;

    // Umidade de Capacidade de campo (CC, %)
    @Column(name = "UMIDADE_CAPACIDADE_CAMPO", nullable = true)
    double umidadeCapacidadeCampo;

    // Umidade de Ponto de Murcha Permanente (PMP, %)
    @Column(name = "UMIDADE_PONTO_MURCHA_PERMANENTE", nullable = true)
    double umidadePontoMurchaPermanente;

    // Agua Disponivel (%)
    @Column(name = "AGUA_DISPONIVEL", nullable = true)
    double aguaDisponivel;

    // Resistência à penetração (MPa)
    @Column(name = "RESISTENCIA_PENETRACAO", nullable = true)
    double resistenciaPenetracao;

    // Percentagem de agregados 6,0 mm de diámetro
    @Column(name = "PERC_AGREGADOS_6_0MM", nullable = true)
    double percAgregados6_0mm;

    // Percentagem de agregados 4,1 a 6.0 mm
    @Column(name = "PERC_AGREGADOS_4_1_A_6_0MM", nullable = true)
    double percAgregados4_1a6_0mm;

    // Percentagem de agregados 2,1a 4.0 mm
    @Column(name = "PERC_AGREGADOS_2_1_A_4_0MM", nullable = true)
    double percAgregados2_1a4_0mm;

    // Percentagem de 1.0 a 2,0 mm
    @Column(name = "PERC_AGREGADOS_1_0_A_2_0MM", nullable = true)
    double percAgregados1_0a2_0mm;

    // Percentagem de agregados < 1,0 mm (Assumindo que "1,0 mm" seja o limite inferior)
    @Column(name = "PERC_AGREGADOS_MENOR_1_0MM", nullable = true)
    double percAgregadosMenor1_0mm;

    public PhysicalAnalysisExtractResponseDto toDto() {
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

        return PhysicalAnalysisExtractResponseDto.builder()
                .id(this.id)
                .rangeExtractId(rangeExtractId)
                .layerExtractId(layerExtractId)
                .initialDepth(initialDepth)
                .finalDepth(finalDepth)
                .layer(camada)
                .subLayer(subLayer)
                .teorAreia(this.teorAreia)
                .teorSilte(this.teorSilte)
                .teorArgila(this.teorArgila)
                .densidadeAparente(this.densidadeAparente)
                .densidadeReal(this.densidadeReal)
                .porosidadeTotal(this.porosidadeTotal)
                .microporosidade(this.microporosidade)
                .umidadeCapacidadeCampo(this.umidadeCapacidadeCampo)
                .umidadePontoMurchaPermanente(this.umidadePontoMurchaPermanente)
                .aguaDisponivel(this.aguaDisponivel)
                .resistenciaPenetracao(this.resistenciaPenetracao)
                .percAgregados6_0mm(this.percAgregados6_0mm)
                .percAgregados4_1a6_0mm(this.percAgregados4_1a6_0mm)
                .percAgregados2_1a4_0mm(this.percAgregados2_1a4_0mm)
                .percAgregados1_0a2_0mm(this.percAgregados1_0a2_0mm)
                .percAgregadosMenor1_0mm(this.percAgregadosMenor1_0mm)
                .build();
    }

}