package com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria;

import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.availablePAnionExchangeResinExtractor.AvailablePAnionExchangeResinExtractorResponseDto;
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
@Table(name = "FOSFORO_DISPONIVEL_COM_EXTRATOR_RESINA_TROCA_ANIONICA")
public class AvailablePAnionExchangeResinExtractorModel {

    private static final String DEFAULT_UNIT = "g/dm3";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne
    @JoinColumn(name = "ID_TABELA", nullable = false)
    SoilFertilityInterpretationCriteriaTableModel table;

    /**
     * Interpretação geral de fósforo disponível por resina de troca aniônica.
     * Critério único, independente da cultura.
     */
    @Column(name = "UNIDADE", nullable = false)
    @Builder.Default
    String unit = DEFAULT_UNIT;

    @Column(name = "MUITO_BAIXO", nullable = false)
    Double pContentTooLow;

    @Column(name = "BAIXO_MENOR", nullable = false)
    Double pContentLowI;

    @Column(name = "BAIXO_MAIOR", nullable = false)
    Double pContentLowF;

    @Column(name = "MEDIO_MENOR", nullable = false)
    Double pContentMediumI;

    @Column(name = "MEDIO_MAIOR", nullable = false)
    Double pContentMediumF;

    @Column(name = "ALTO_MENOR", nullable = false)
    Double pContentHighI;

    @Column(name = "ALTO_MAIOR", nullable = false)
    Double pContentHighF;

    @Column(name = "MUITO_ALTO", nullable = false)
    Double pContentTooHigh;

    @PrePersist
    @PreUpdate
    private void normalizeUnit() {
        this.unit = DEFAULT_UNIT;
    }

    public AvailablePAnionExchangeResinExtractorResponseDto toDto() {
        return AvailablePAnionExchangeResinExtractorResponseDto.builder()
                .id(this.id)
                .tableId(this.table != null ? this.table.getId() : null)
                .unit(DEFAULT_UNIT)
                .pContentTooLow(this.pContentTooLow)
                .pContentLowI(this.pContentLowI)
                .pContentLowF(this.pContentLowF)
                .pContentMediumI(this.pContentMediumI)
                .pContentMediumF(this.pContentMediumF)
                .pContentHighI(this.pContentHighI)
                .pContentHighF(this.pContentHighF)
                .pContentTooHigh(this.pContentTooHigh)
                .build();
    }
}
