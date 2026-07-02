package com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables;

import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.MenorMaiorTeores;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.NomeComum;
import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.UnidadeTeor;
import com.migueltcc.fertintelligence.dto.tables.cropFoliarAnalysisInterpretation.tableLine.CropFoliarAnalysisInterpretationTableLineResponseDto;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
@Data
@Table(name = "LINHAS_TABELAS_DE_INTERPRETACAO_DE_ANÁLISE_FOLIAR_DE_CULTURAS")
public class CropFoliarAnalysisInterpretationTableLineModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TABELA_INTERPRETACAO", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    CropFoliarAnalysisInterpretationTableModel table;

    /**
     * Regras de Negócio:
     * A tabela de interpretação de análise foliar de culturas deve possuir ao menos uma linha.
     * A tabela não pode possuir linhas com nomes de cultura repetidos.
     */
    @Column(name = "NOME_CULTURA", nullable = false)
    private NomeComum crop;

    // --- NITROGÊNIO (N) ---
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "menor", column = @Column(name = "N_MENOR_TEOR")),
            @AttributeOverride(name = "maior", column = @Column(name = "N_MAIOR_TEOR")),
            @AttributeOverride(name = "unity", column = @Column(name = "N_UNIDADE_TEORES"))
    })
    private MenorMaiorTeores n_content;

    // --- FÓSFORO (P) ---
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "menor", column = @Column(name = "P_MENOR_TEOR")),
            @AttributeOverride(name = "maior", column = @Column(name = "P_MAIOR_TEOR")),
            @AttributeOverride(name = "unity", column = @Column(name = "P_UNIDADE_TEORES"))
    })
    private MenorMaiorTeores p_content;

    // --- POTÁSSIO (K) ---
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "menor", column = @Column(name = "K_MENOR_TEOR")),
            @AttributeOverride(name = "maior", column = @Column(name = "K_MAIOR_TEOR")),
            @AttributeOverride(name = "unity", column = @Column(name = "K_UNIDADE_TEORES"))
    })
    private MenorMaiorTeores k_content;

    // --- CÁLCIO (Ca) ---
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "menor", column = @Column(name = "Ca_MENOR_TEOR")),
            @AttributeOverride(name = "maior", column = @Column(name = "Ca_MAIOR_TEOR")),
            @AttributeOverride(name = "unity", column = @Column(name = "Ca_UNIDADE_TEORES"))
    })
    private MenorMaiorTeores ca_content;

    // --- MAGNÉSIO (Mg) ---
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "menor", column = @Column(name = "MG_MENOR_TEOR")),
            @AttributeOverride(name = "maior", column = @Column(name = "MG_MAIOR_TEOR")),
            @AttributeOverride(name = "unity", column = @Column(name = "MG_UNIDADE_TEORES"))
    })
    private MenorMaiorTeores mg_content;

    // --- ENXOFRE (S) ---
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "menor", column = @Column(name = "S_MENOR_TEOR")),
            @AttributeOverride(name = "maior", column = @Column(name = "S_MAIOR_TEOR")),
            @AttributeOverride(name = "unity", column = @Column(name = "S_UNIDADE_TEORES"))
    })
    private MenorMaiorTeores s_content;

    // --- BORO (B) ---
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "menor", column = @Column(name = "B_MENOR_TEOR")),
            @AttributeOverride(name = "maior", column = @Column(name = "B_MAIOR_TEOR")),
            @AttributeOverride(name = "unity", column = @Column(name = "B_UNIDADE_TEORES"))
    })
    private MenorMaiorTeores b_content;

    // --- COBRE (Cu) ---
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "menor", column = @Column(name = "CU_MENOR_TEOR")),
            @AttributeOverride(name = "maior", column = @Column(name = "CU_MAIOR_TEOR")),
            @AttributeOverride(name = "unity", column = @Column(name = "CU_UNIDADE_TEORES"))
    })
    private MenorMaiorTeores cu_content;

    // --- FERRO (Fe) ---
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "menor", column = @Column(name = "FE_MENOR_TEOR")),
            @AttributeOverride(name = "maior", column = @Column(name = "FE_MAIOR_TEOR")),
            @AttributeOverride(name = "unity", column = @Column(name = "FE_UNIDADE_TEORES"))
    })
    private MenorMaiorTeores fe_content;

    // --- MANGANÊS (Mn) ---
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "menor", column = @Column(name = "MN_MENOR_TEOR")),
            @AttributeOverride(name = "maior", column = @Column(name = "MN_MAIOR_TEOR")),
            @AttributeOverride(name = "unity", column = @Column(name = "MN_UNIDADE_TEORES"))
    })
    private MenorMaiorTeores mn_content;

    // --- MOLIBDÊNIO (Mo) ---
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "menor", column = @Column(name = "MO_MENOR_TEOR")),
            @AttributeOverride(name = "maior", column = @Column(name = "MO_MAIOR_TEOR")),
            @AttributeOverride(name = "unity", column = @Column(name = "MO_UNIDADE_TEORES"))
    })
    private MenorMaiorTeores mo_content;

    // --- ZINCO (Zn) ---
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "menor", column = @Column(name = "ZN_MENOR_TEOR")),
            @AttributeOverride(name = "maior", column = @Column(name = "ZN_MAIOR_TEOR")),
            @AttributeOverride(name = "unity", column = @Column(name = "ZN_UNIDADE_TEORES"))
    })
    private MenorMaiorTeores zn_content;

    public CropFoliarAnalysisInterpretationTableLineResponseDto toDto() {
        return CropFoliarAnalysisInterpretationTableLineResponseDto.builder()
                .id(this.id)
                .table_id(this.table != null ? this.table.getId() : null)
                .crop(this.crop)
                .n_content(copyMacronutrientRange(this.n_content))
                .p_content(copyMacronutrientRange(this.p_content))
                .k_content(copyMacronutrientRange(this.k_content))
                .ca_content(copyMacronutrientRange(this.ca_content))
                .mg_content(copyMacronutrientRange(this.mg_content))
                .s_content(copyMacronutrientRange(this.s_content))
                .b_content(this.b_content)
                .cu_content(this.cu_content)
                .fe_content(this.fe_content)
                .mn_content(this.mn_content)
                .mo_content(this.mo_content)
                .zn_content(this.zn_content)
                .build();
    }

    private MenorMaiorTeores copyMacronutrientRange(MenorMaiorTeores range) {
        if (range == null) {
            return null;
        }
        return new MenorMaiorTeores(range.getMenor(), range.getMaior(), UnidadeTeor.g_per_kg);
    }
}
