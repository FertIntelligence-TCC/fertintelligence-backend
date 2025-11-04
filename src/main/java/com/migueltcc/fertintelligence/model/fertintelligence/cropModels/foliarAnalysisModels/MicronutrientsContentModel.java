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
@Table(name = "MACRONUTRIENTES")
@EqualsAndHashCode
public class MicronutrientsContentModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne
    @JoinColumn(name = "ID_ANALISE_FOLIAR", nullable = false)
    FoliarAnalysisModel analysis;

    Double b_content;

    Double cu_content;

    Double fe_content;

    Double ni_content;

    Double mn_content;

    Double mo_content;

    Double zn_content;

}
