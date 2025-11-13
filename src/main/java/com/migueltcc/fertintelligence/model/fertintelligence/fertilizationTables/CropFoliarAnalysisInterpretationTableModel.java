package com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables;

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
@Table(name = "TABELAS_DE_INTERPRETACAO_DE_ANÁLISE_FOLIAR_DE CULTURAS")
public class CropFoliarAnalysisInterpretationTableModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
}
