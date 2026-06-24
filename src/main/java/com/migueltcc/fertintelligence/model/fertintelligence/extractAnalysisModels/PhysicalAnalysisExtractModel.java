package com.migueltcc.fertintelligence.model.fertintelligence.extractAnalysisModels;

import com.migueltcc.fertintelligence.dto.extractAnalysis.physical.PhysicalAnalysisExtractResponseDto;
import com.migueltcc.fertintelligence.composedAttributes.physicalAnalysis.PhysicalAnalysisUnit;
import com.migueltcc.fertintelligence.composedAttributes.physicalAnalysis.PhysicalAnalysisUnitConverter;
import com.migueltcc.fertintelligence.model.fertintelligence.extractModels.LayerExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractModels.RangeExtractModel;
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

    private static final PhysicalAnalysisUnit DEFAULT_PHYSICAL_UNIT = PhysicalAnalysisUnit.G_PER_DM3;

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
    Double teorAreia;

    @Convert(converter = PhysicalAnalysisUnitConverter.class)
    @Column(name = "UNIDADE_TEOR_DE_AREIA", nullable = false)
    @Builder.Default
    PhysicalAnalysisUnit unidadeTeorAreia = DEFAULT_PHYSICAL_UNIT;

    // Teor de Silte (g/dm3)
    @Column(name = "TEOR_DE_SILTE", nullable = true)
    Double teorSilte;

    @Convert(converter = PhysicalAnalysisUnitConverter.class)
    @Column(name = "UNIDADE_TEOR_DE_SILTE", nullable = false)
    @Builder.Default
    PhysicalAnalysisUnit unidadeTeorSilte = DEFAULT_PHYSICAL_UNIT;

    // Teor de Argila (g/dm3)
    @Column(name = "TEOR_DE_ARGILA", nullable = true)
    Double teorArgila;

    @Convert(converter = PhysicalAnalysisUnitConverter.class)
    @Column(name = "UNIDADE_TEOR_DE_ARGILA", nullable = false)
    @Builder.Default
    PhysicalAnalysisUnit unidadeTeorArgila = DEFAULT_PHYSICAL_UNIT;

    // Densidade Aparente (g/cm3)
    @Column(name = "DENSIDADE_APARENTE", nullable = true)
    Double densidadeAparente;

    @Convert(converter = PhysicalAnalysisUnitConverter.class)
    @Column(name = "UNIDADE_DENSIDADE_APARENTE", nullable = false)
    @Builder.Default
    PhysicalAnalysisUnit unidadeDensidadeAparente = DEFAULT_PHYSICAL_UNIT;

    // Densidade Real (g/cm3)
    @Column(name = "DENSIDADE_REAL", nullable = true)
    Double densidadeReal;

    @Convert(converter = PhysicalAnalysisUnitConverter.class)
    @Column(name = "UNIDADE_DENSIDADE_REAL", nullable = false)
    @Builder.Default
    PhysicalAnalysisUnit unidadeDensidadeReal = DEFAULT_PHYSICAL_UNIT;

    // Porosidade Total (%)
    @Column(name = "POROSIDADE_TOTAL", nullable = true)
    Double porosidadeTotal;

    // Microporosidade (%)
    @Column(name = "MICROPOROSIDADE", nullable = true)
    Double microporosidade;

    // Umidade de Capacidade de campo (CC, %)
    @Column(name = "UMIDADE_CAPACIDADE_CAMPO", nullable = true)
    Double umidadeCapacidadeCampo;

    // Umidade de Ponto de Murcha Permanente (PMP, %)
    @Column(name = "UMIDADE_PONTO_MURCHA_PERMANENTE", nullable = true)
    Double umidadePontoMurchaPermanente;

    // Agua Disponivel (%)
    @Column(name = "AGUA_DISPONIVEL", nullable = true)
    Double aguaDisponivel;

    // Resistência à penetração (MPa)
    @Column(name = "RESISTENCIA_PENETRACAO", nullable = true)
    Double resistenciaPenetracao;

    // Percentagem de agregados 6,0 mm de diámetro
    @Column(name = "PERC_AGREGADOS_6_0MM", nullable = true)
    Double percAgregados6_0mm;

    // Percentagem de agregados 4,1 a 6.0 mm
    @Column(name = "PERC_AGREGADOS_4_1_A_6_0MM", nullable = true)
    Double percAgregados4_1a6_0mm;

    // Percentagem de agregados 2,1a 4.0 mm
    @Column(name = "PERC_AGREGADOS_2_1_A_4_0MM", nullable = true)
    Double percAgregados2_1a4_0mm;

    // Percentagem de 1.0 a 2,0 mm
    @Column(name = "PERC_AGREGADOS_1_0_A_2_0MM", nullable = true)
    Double percAgregados1_0a2_0mm;

    // Percentagem de agregados 0,5 a 1,0 mm
    @Column(name = "PERC_AGREGADOS_0_5_A_1_0MM", nullable = true)
    Double percAgregados0_5a1_0mm;

    // Percentagem de agregados 0,25 a 0,5 mm
    @Column(name = "PERC_AGREGADOS_0_25_A_0_5MM", nullable = true)
    Double percAgregados0_25a0_5mm;

    // Percentagem de agregados < 0,25 mm
    @Column(name = "PERC_AGREGADOS_MENOR_0_25MM", nullable = true)
    Double percAgregadosMenor0_25mm;

    // Diâmetro médio dos agregados (mm)
    @Column(name = "DM_AGREGADOS", nullable = true)
    Double dmAgregados;

    @PrePersist
    @PreUpdate
    public void recalculateComputedFields() {
        normalizePhysicalUnits();

        double densidadeRealValue = zeroIfNull(this.densidadeReal);
        double densidadeAparenteValue = zeroIfNull(this.densidadeAparente);

        this.porosidadeTotal = densidadeRealValue != 0.0
                ? ((densidadeRealValue - densidadeAparenteValue) / densidadeRealValue) * 100.0
                : 0.0;

        this.aguaDisponivel =
                zeroIfNull(this.umidadeCapacidadeCampo)
                        - zeroIfNull(this.umidadePontoMurchaPermanente);

        this.dmAgregados =
                ((zeroIfNull(this.percAgregados6_0mm) * 6.0)
                        + (zeroIfNull(this.percAgregados4_1a6_0mm) * 5.05)
                        + (zeroIfNull(this.percAgregados2_1a4_0mm) * 3.05)
                        + (zeroIfNull(this.percAgregados1_0a2_0mm) * 1.5)
                        + (zeroIfNull(this.percAgregados0_5a1_0mm) * 0.75)
                        + (zeroIfNull(this.percAgregados0_25a0_5mm) * 0.375)
                        + (zeroIfNull(this.percAgregadosMenor0_25mm) * 0.125))
                        / 100.0;
    }

    private double zeroIfNull(Double value) {
        return value != null ? value : 0.0;
    }

    private void normalizePhysicalUnits() {
        this.unidadeTeorAreia = normalizePhysicalUnit(this.unidadeTeorAreia);
        this.unidadeTeorSilte = normalizePhysicalUnit(this.unidadeTeorSilte);
        this.unidadeTeorArgila = normalizePhysicalUnit(this.unidadeTeorArgila);
        this.unidadeDensidadeAparente = normalizePhysicalUnit(this.unidadeDensidadeAparente);
        this.unidadeDensidadeReal = normalizePhysicalUnit(this.unidadeDensidadeReal);
    }

    private PhysicalAnalysisUnit normalizePhysicalUnit(PhysicalAnalysisUnit unit) {
        return unit != null ? unit.canonicalForPhysicalExtract() : DEFAULT_PHYSICAL_UNIT;
    }

    public PhysicalAnalysisExtractResponseDto toDto() {
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

        return PhysicalAnalysisExtractResponseDto.builder()
                .id(this.id)
                .rangeExtractId(rangeExtractId)
                .layerExtractId(layerExtractId)
                .initialDepth(initialDepth)
                .finalDepth(finalDepth)
                .layer(camada)
                .subLayer(subLayer)
                .teorAreia(this.teorAreia)
                .unidadeTeorAreia(normalizePhysicalUnit(this.unidadeTeorAreia))
                .teorSilte(this.teorSilte)
                .unidadeTeorSilte(normalizePhysicalUnit(this.unidadeTeorSilte))
                .teorArgila(this.teorArgila)
                .unidadeTeorArgila(normalizePhysicalUnit(this.unidadeTeorArgila))
                .densidadeAparente(this.densidadeAparente)
                .unidadeDensidadeAparente(normalizePhysicalUnit(this.unidadeDensidadeAparente))
                .densidadeReal(this.densidadeReal)
                .unidadeDensidadeReal(normalizePhysicalUnit(this.unidadeDensidadeReal))
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
                .percAgregados0_5a1_0mm(this.percAgregados0_5a1_0mm)
                .percAgregados0_25a0_5mm(this.percAgregados0_25a0_5mm)
                .percAgregadosMenor0_25mm(this.percAgregadosMenor0_25mm)
                .dmAgregados(this.dmAgregados)
                .dmpAgregados(this.dmAgregados)
                .build();
    }

}
