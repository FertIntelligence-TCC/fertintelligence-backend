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
@Table(name = "TEOR_ELEMENTOS_BENEFICOS")
@EqualsAndHashCode
public class BeneficialElementsContentModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne
    @JoinColumn(name = "ID_ANALISE_FOLIAR", nullable = false)
    FoliarAnalysisModel analysis;

    @Column(name = "TEOR_SODIO", nullable = true)
    Double na_content;

    @Column(name = "TEOR_SILICIO", nullable = true)
    Double si_content;

    @Column(name = "TEOR_VANADIO", nullable = true)
    Double v_content;

    @Column(name = "TEOR_COBALTO", nullable = true)
    Double co_content;

    @Column(name = "TEOR_SELENIO", nullable = true)
    Double se_content;

}
