package com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables;

import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.NomeComum;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.NutrienteFolha;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
@Data
@Table(name = "LINHAS_TABELAS_DE_INTERPRETACAO_DE_ANÁLISE_FOLIAR_DE CULTURAS")
public class CropFoliarAnalysisInterpretationTableLineModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "ID_TABELA_DE_INTERPRETACAO_DE_ANÁLISE_FOLIAR_DE CULTURAS", nullable = false)
    CropFoliarAnalysisInterpretationTableModel table;

    @Column(name = "NOME_CULTURA", nullable = false)
    NomeComum crop;

    @Column(name = "NUTRIENTE_CULTURA", nullable = false)
    NutrienteFolha nutrient;

    @Column(name = "MENOR_DOSE", nullable = false)
    Double lower_dose;

    @Column(name = "MAIOR_DOSE", nullable = false)
    Double higher_dose;

}
