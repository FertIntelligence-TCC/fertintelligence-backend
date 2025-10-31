package com.migueltcc.fertintelligence.model.fertintelligence.ExtractModels;

import com.migueltcc.fertintelligence.composedAttributes.SoilExtracts.Camada;
import com.migueltcc.fertintelligence.model.fertintelligence.SoilAnalysisModel;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
@Table(name = "EXTRATOS_CAMADAS")
@EqualsAndHashCode
public class LayerExtractModel {

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

    // Camada O, A, B, E, C
    @Column(name = "CAMADA", nullable = false)
    Camada layer;

    @Column(name = "SUBCAMADA", nullable = false)
    Integer sub_layer;
}
