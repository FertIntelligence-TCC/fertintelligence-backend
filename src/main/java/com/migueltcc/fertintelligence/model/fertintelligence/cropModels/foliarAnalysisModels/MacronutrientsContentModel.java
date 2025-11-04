package com.migueltcc.fertintelligence.model.fertintelligence.cropModels.foliarAnalysisModels;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@Builder
@Entity
@Data
@Table(name = "TEOR_MACRONUTRIENTES")
@EqualsAndHashCode
public class MacronutrientsContentModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne
    @JoinColumn(name = "ID_ANALISE_FOLIAR", nullable = false)
    FoliarAnalysisModel analysis;

    @Column(name = "TEOR_NITROGENIO", nullable = true)
    Double n_content;

    @Column(name = "TEOR_FOSFORO", nullable = true)
    Double p_content;

    @Column(name = "TEOR_POTASSIO", nullable = true)
    Double k_content;

    @Column(name = "TEOR_CALCIO", nullable = true)
    Double ca_content;

    @Column(name = "TEOR_MAGNESIO", nullable = true)
    Double mg_content;

    @Column(name = "TEOR_ENXOFRE", nullable = true)
    Double s_content;

}
