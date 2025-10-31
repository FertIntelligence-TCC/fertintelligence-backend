package com.migueltcc.fertintelligence.model.fertintelligence.AnalysisExtractModels;

import com.migueltcc.fertintelligence.model.fertintelligence.ExtractModels.ExtractTemplateModel;
import com.migueltcc.fertintelligence.model.fertintelligence.SoilAnalysisModel;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
@Table(name = "EXTRATOS_ANALISES_FISICAS")
@EqualsAndHashCode
public class PhysicAnalysisExtractModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne
    @JoinColumn(name = "ID_EXTRATO", nullable = false)
    ExtractTemplateModel extract;

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
}