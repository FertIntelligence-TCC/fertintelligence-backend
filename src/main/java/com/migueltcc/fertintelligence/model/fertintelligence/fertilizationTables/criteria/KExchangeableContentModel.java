package com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria;

import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.kExchangeableContentModel.KExchangeableContentResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.SoilFertilityInterpretationCriteriaTableModel;
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
@Table(name = "TEORES_TROCAVEIS_DE_POTASSIO")
public class KExchangeableContentModel {

    public static final String DEFAULT_UNIT = "mmolc/dm³";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TABELA", nullable = false)
    SoilFertilityInterpretationCriteriaTableModel table;

    @Column(name = "UNIDADE", nullable = false)
    @Builder.Default
    String unit = DEFAULT_UNIT;

    /**
     * Interpretação dos teores de Potássio (K).
     * Critério único, independente da CTC.
     */
    @Column(name = "MENOR_TEOR_K", nullable = false)
    Double kContentTooLow;

    @Column(name = "TEOR_INICIAL_BAIXO_K", nullable = false)
    Double kContentLowI;

    @Column(name = "TEOR_FINAL_BAIXO_K", nullable = false)
    Double kContentLowF;

    @Column(name = "TEOR_INICIAL_MEDIO_K", nullable = false)
    Double kContentMediumI;

    @Column(name = "TEOR_FINAL_MEDIO_K", nullable = false)
    Double kContentMediumF;

    @Column(name = "TEOR_INICIAL_ALTO_K", nullable = false)
    Double kContentHighI;

    @Column(name = "TEOR_FINAL_ALTO_K", nullable = false)
    Double kContentHighF;

    @Column(name = "MAIOR_TEOR_K", nullable = false)
    Double kContentTooHigh;

    @Column(name = "OBSERVACOES", length = 1000)
    String observations;

    @Column(name = "FONTES", length = 1000)
    String sources;

    @PrePersist
    @PreUpdate
    private void normalizeUnit() {
        this.unit = DEFAULT_UNIT;
    }

    public KExchangeableContentResponseDto toDto() {
        return KExchangeableContentResponseDto.builder()
                .id(this.id)
                .tableId(this.table != null ? this.table.getId() : null)
                .unit(DEFAULT_UNIT)
                .kContentTooLow(this.kContentTooLow)
                .kContentLowI(this.kContentLowI)
                .kContentLowF(this.kContentLowF)
                .kContentMediumI(this.kContentMediumI)
                .kContentMediumF(this.kContentMediumF)
                .kContentHighI(this.kContentHighI)
                .kContentHighF(this.kContentHighF)
                .kContentTooHigh(this.kContentTooHigh)
                .observations(this.observations)
                .sources(this.sources)
                .build();
    }
}
