package com.migueltcc.fertintelligence.model.fertintelligence.AnalysisExtractModels;

import com.migueltcc.fertintelligence.model.fertintelligence.ExtractModels.LayerExtractModel;
import com.migueltcc.fertintelligence.model.fertintelligence.ExtractModels.RangeExtractModel;
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne
    @JoinColumn(name = "ID_EXTRATO_INTERVALOS", nullable = true)
    RangeExtractModel rangeExtract;

    @OneToOne
    @JoinColumn(name = "ID_EXTRATO_CAMADAS", nullable = true)
    LayerExtractModel layerExtract;

    // pH água
    @Column(name = "PH_AGUA", nullable = true)
    Double phAgua;

    // pH CaCl2 0,01 M
    @Column(name = "PH_CACL2", nullable = true)
    Double phCacl2;

    // Ca2+ (número e + sobrescrito), mmolc/dm3
    @Column(name = "CALCIO", nullable = true)
    Double calcio;

    // Mg2+ (número e + sobrescrito), mmolc/dm3
    @Column(name = "MAGNESIO", nullable = true)
    Double magnesio;

    // K+ (número e + sobrescrito), mmolc/dm3
    @Column(name = "POTASSIO", nullable = true)
    Double potassio;

    // Na+ (número e + sobrescrito), mmolc/dm3
    @Column(name = "SODIO", nullable = true)
    Double sodio;

    // Al3+ (número e + sobrescrito), mmolc/dm3
    @Column(name = "ALUMINIO", nullable = true)
    Double aluminio;

    // Al+H, mmolc/dm3
    @Column(name = "ALUMINIO_MAIS_HIDROGENIO", nullable = true)
    Double aluminioMaisHidrogenio;

    // Soma de Bases, SB, mmolc/dm3
    @Column(name = "SOMA_BASES", nullable = true)
    Double somaBases;

    // CTC efetiva (t), mmolc/dm3
    @Column(name = "CTC_EFETIVA", nullable = true)
    Double ctcEfetiva;

    // CTC pH 7,0 (T), mmol/dm3,
    @Column(name = "CTC_PH_7", nullable = true)
    Double ctcPh7;

    // Valor de saturação de bases trocáveis (V),
    @Column(name = "SATURACAO_BASES_V", nullable = true)
    Double saturacaoBasesV;

    // Valor de saturação por alumínio trocável (m),
    @Column(name = "SATURACAO_ALUMINIO_M", nullable = true)
    Double saturacaoAluminioM;

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
}