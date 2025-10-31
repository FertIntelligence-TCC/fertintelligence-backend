package com.migueltcc.fertintelligence.model.fertintelligence.ExtractModels;

import com.migueltcc.fertintelligence.model.fertintelligence.SoilAnalysisModel;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Data
@Table(name = "TEMPLATES_EXTRATOS")
@EqualsAndHashCode
public class ExtractTemplateModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "ID_ANALISE", nullable = false)
    SoilAnalysisModel analysis;

    // Profundidade Inicial, em cm
    @Column(name = "PROFUNDIDADE_INICIAL", nullable = false)
    Integer profundidade_inicial;

    // Profundidade Final, em cm
    @Column(name = "PROFUNDIDADE_FINAL", nullable = false)
    Integer profundidade_final;

}
