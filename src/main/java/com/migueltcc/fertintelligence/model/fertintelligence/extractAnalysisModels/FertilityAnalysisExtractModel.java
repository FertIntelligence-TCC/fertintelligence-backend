package com.migueltcc.fertintelligence.model.fertintelligence.extractAnalysisModels;

import com.migueltcc.fertintelligence.composedAttributes.soilExtracts.Camada;
import com.migueltcc.fertintelligence.composedAttributes.fertilityAnalysis.FertilityAnalysisUnit;
import com.migueltcc.fertintelligence.composedAttributes.fertilityAnalysis.FertilityAnalysisUnitConverter;
import com.migueltcc.fertintelligence.dto.extractAnalysis.fertility.FertilityAnalysisExtractResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.extractModels.LayerExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.extractModels.RangeExtractModel;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
@Table(name = "EXTRATOS_ANALISES_FERTILIDADE")
@EqualsAndHashCode
public class FertilityAnalysisExtractModel {

    private static final FertilityAnalysisUnit DEFAULT_EXCHANGE_COMPLEX_UNIT = FertilityAnalysisUnit.MMOLC_PER_DM3;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_EXTRATO_INTERVALOS", nullable = true) // rangeExtract ou layerExtract deve ser nulo!
    RangeExtractModel rangeExtract;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_EXTRATO_CAMADAS", nullable = true) // rangeExtract ou layerExtract deve ser nulo!
    LayerExtractModel layerExtract;

    // pH água
    @Column(name = "PH_AGUA", nullable = true)
    Double phAgua;

    // pH CaCl2 0,01 M
    @Column(name = "PH_CACL2", nullable = true)
    Double phCacl2;

    // Ca2+ (número e + sobrescrito), mmolc/dm³
    @Column(name = "CALCIO", nullable = true)
    Double calcio;

    @Convert(converter = FertilityAnalysisUnitConverter.class)
    @Column(name = "UNIDADE_CALCIO", nullable = false)
    @Builder.Default
    FertilityAnalysisUnit unidadeCalcio = DEFAULT_EXCHANGE_COMPLEX_UNIT;

    // Mg2+ (número e + sobrescrito), mmolc/dm³
    @Column(name = "MAGNESIO", nullable = true)
    Double magnesio;

    @Convert(converter = FertilityAnalysisUnitConverter.class)
    @Column(name = "UNIDADE_MAGNESIO", nullable = false)
    @Builder.Default
    FertilityAnalysisUnit unidadeMagnesio = DEFAULT_EXCHANGE_COMPLEX_UNIT;

    // K+ (número e + sobrescrito), mmolc/dm³
    @Column(name = "POTASSIO", nullable = true)
    Double potassio;

    @Convert(converter = FertilityAnalysisUnitConverter.class)
    @Column(name = "UNIDADE_POTASSIO", nullable = false)
    @Builder.Default
    FertilityAnalysisUnit unidadePotassio = DEFAULT_EXCHANGE_COMPLEX_UNIT;

    // Na+ (número e + sobrescrito), mmolc/dm³
    @Column(name = "SODIO", nullable = true)
    Double sodio;

    @Convert(converter = FertilityAnalysisUnitConverter.class)
    @Column(name = "UNIDADE_SODIO", nullable = false)
    @Builder.Default
    FertilityAnalysisUnit unidadeSodio = DEFAULT_EXCHANGE_COMPLEX_UNIT;

    // Al3+ (número e + sobrescrito), mmolc/dm³
    @Column(name = "ALUMINIO", nullable = true)
    Double aluminio;

    @Convert(converter = FertilityAnalysisUnitConverter.class)
    @Column(name = "UNIDADE_ALUMINIO", nullable = false)
    @Builder.Default
    FertilityAnalysisUnit unidadeAluminio = DEFAULT_EXCHANGE_COMPLEX_UNIT;

    // Al+H, mmolc/dm³
    @Column(name = "ALUMINIO_MAIS_HIDROGENIO", nullable = true)
    Double aluminioMaisHidrogenio;

    @Convert(converter = FertilityAnalysisUnitConverter.class)
    @Column(name = "UNIDADE_ALUMINIO_MAIS_HIDROGENIO", nullable = false)
    @Builder.Default
    FertilityAnalysisUnit unidadeAluminioMaisHidrogenio = DEFAULT_EXCHANGE_COMPLEX_UNIT;

    // Soma de Bases, SB, mmolc/dm³
    @Column(name = "SOMA_BASES", nullable = true)
    Double somaBases;

    @Convert(converter = FertilityAnalysisUnitConverter.class)
    @Column(name = "UNIDADE_SOMA_BASES", nullable = false)
    @Builder.Default
    FertilityAnalysisUnit unidadeSomaBases = DEFAULT_EXCHANGE_COMPLEX_UNIT;

    // CTC efetiva (t), mmolc/dm³
    @Column(name = "CTC_EFETIVA", nullable = true)
    Double ctcEfetiva;

    @Convert(converter = FertilityAnalysisUnitConverter.class)
    @Column(name = "UNIDADE_CTC_EFETIVA", nullable = false)
    @Builder.Default
    FertilityAnalysisUnit unidadeCtcEfetiva = DEFAULT_EXCHANGE_COMPLEX_UNIT;

    // CTC pH 7,0 (T), mmolc/dm³
    @Column(name = "CTC_PH_7", nullable = true)
    Double ctcPh7;

    @Convert(converter = FertilityAnalysisUnitConverter.class)
    @Column(name = "UNIDADE_CTC_PH_7", nullable = false)
    @Builder.Default
    FertilityAnalysisUnit unidadeCtcPh7 = DEFAULT_EXCHANGE_COMPLEX_UNIT;

    // Valor de saturação de bases trocáveis (V),
    @Column(name = "SATURACAO_BASES_V", nullable = true)
    Double saturacaoBasesV;

    // Valor de saturação por alumínio trocável (m),
    @Column(name = "SATURACAO_ALUMINIO_M", nullable = true)
    Double saturacaoAluminioM;

    // Percentagem de sódio trocável (PST), %
    @Column(name = "PST", nullable = true)
    Double pst;

    // P disponível (Extrator Mehlich-1), mg/dm3
    @Column(name = "FOSFORO_MEHLICH1", nullable = true)
    Double fosforoMehlich1;

    // P disponível (extrator Resina Troca Aniônica), mg/dm3,
    @Column(name = "FOSFORO_RESINA", nullable = true)
    Double fosforoResina;

    // Enxofre disponível (S), mg/dm3,
    @Column(name = "ENXOFRE", nullable = true)
    Double enxofre;

    // Matéria orgânica (MO, dag/kg),
    @Column(name = "MATERIA_ORGANICA", nullable = true)
    Double materiaOrganica;

    // Boro disponível (B, mg/dm3),
    @Column(name = "BORO", nullable = true)
    Double boro;

    // Cobre disponível (Cu, mg/dm3),
    @Column(name = "COBRE", nullable = true)
    Double cobre;

    // Ferro disponível (Fe, mg/dm3),
    @Column(name = "FERRO", nullable = true)
    Double ferro;

    // Manganês disponível (Mn, mg/dm3),
    @Column(name = "MANGANES", nullable = true)
    Double manganes;

    // Molibdênio disponível (Mo, mg/dm3)
    @Column(name = "MOLIBDENIO", nullable = true)
    Double molibdenio;

    // Zinco disponível (Zn, mg/dm3).
    @Column(name = "ZINCO", nullable = true)
    Double zinco;

    @PrePersist
    @PreUpdate
    public void normalizeFertilityUnits() {
        this.unidadeCalcio = normalizeFertilityUnit(this.unidadeCalcio);
        this.unidadeMagnesio = normalizeFertilityUnit(this.unidadeMagnesio);
        this.unidadePotassio = normalizeFertilityUnit(this.unidadePotassio);
        this.unidadeSodio = normalizeFertilityUnit(this.unidadeSodio);
        this.unidadeAluminio = normalizeFertilityUnit(this.unidadeAluminio);
        this.unidadeAluminioMaisHidrogenio = normalizeFertilityUnit(this.unidadeAluminioMaisHidrogenio);
        this.unidadeSomaBases = normalizeFertilityUnit(this.unidadeSomaBases);
        this.unidadeCtcEfetiva = normalizeFertilityUnit(this.unidadeCtcEfetiva);
        this.unidadeCtcPh7 = normalizeFertilityUnit(this.unidadeCtcPh7);
    }

    private FertilityAnalysisUnit normalizeFertilityUnit(FertilityAnalysisUnit unit) {
        return unit != null ? unit.canonicalForFertilityExtract() : DEFAULT_EXCHANGE_COMPLEX_UNIT;
    }

    public FertilityAnalysisExtractResponseDto toDto() {
        normalizeFertilityUnits();

        RangeExtractModel range = this.rangeExtract;
        LayerExtractModel layer = this.layerExtract;

        Long rangeExtractId = null;
        Long layerExtractId = null;
        Integer initialDepth = null;
        Integer finalDepth = null;
        Camada camada = null;
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

        return FertilityAnalysisExtractResponseDto.builder()
                .id(this.id)
                .rangeExtractId(rangeExtractId)
                .layerExtractId(layerExtractId)
                .initialDepth(initialDepth)
                .finalDepth(finalDepth)
                .layer(camada)
                .subLayer(subLayer)
                .phAgua(this.phAgua)
                .phCacl2(this.phCacl2)
                .calcio(this.calcio)
                .unidadeCalcio(this.unidadeCalcio)
                .magnesio(this.magnesio)
                .unidadeMagnesio(this.unidadeMagnesio)
                .potassio(this.potassio)
                .unidadePotassio(this.unidadePotassio)
                .sodio(this.sodio)
                .unidadeSodio(this.unidadeSodio)
                .aluminio(this.aluminio)
                .unidadeAluminio(this.unidadeAluminio)
                .aluminioMaisHidrogenio(this.aluminioMaisHidrogenio)
                .unidadeAluminioMaisHidrogenio(this.unidadeAluminioMaisHidrogenio)
                .somaBases(this.somaBases)
                .unidadeSomaBases(this.unidadeSomaBases)
                .ctcEfetiva(this.ctcEfetiva)
                .unidadeCtcEfetiva(this.unidadeCtcEfetiva)
                .ctcPh7(this.ctcPh7)
                .unidadeCtcPh7(this.unidadeCtcPh7)
                .saturacaoBasesV(this.saturacaoBasesV)
                .saturacaoAluminioM(this.saturacaoAluminioM)
                .pst(this.pst)
                .fosforoMehlich1(this.fosforoMehlich1)
                .fosforoResina(this.fosforoResina)
                .enxofre(this.enxofre)
                .materiaOrganica(this.materiaOrganica)
                .boro(this.boro)
                .cobre(this.cobre)
                .ferro(this.ferro)
                .manganes(this.manganes)
                .molibdenio(this.molibdenio)
                .zinco(this.zinco)
                .build();
    }

}
