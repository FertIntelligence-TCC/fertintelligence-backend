package com.migueltcc.fertintelligence.model.fertintelligence;

import com.migueltcc.fertintelligence.composedAttributes.SoilExtracts.TipoExtrato;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
@Table(name = "TEMPLATES_ANALISES")
@EqualsAndHashCode
public class SoilAnalysisModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "ID_PROPRIEDADE", nullable = false)
    PropertyModel property;

    @Column(name = "ANO_ANALISE", nullable = false)
    Integer analysisYear;

    @Column(name = "LABORATORIO_RESPONSAVEL", nullable = false)
    String responsibleLaboratory;

    @Column(name = "TIPO_EXTRATO", nullable = false)
    TipoExtrato extractType;

}
