package com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria;

import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.kContentAndDose.KContentAndDoseResponseDto;
import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.kContentAndDose.KContentAndDoseSectionDto;
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
@Table(name = "TEORES_E_DOSES_DE_K")
public class KContentAndDoseModel {

    public static final String DISPLAY_NAME = "Teores e doses de K";
    public static final String CONTENT_UNIT = "mmolc/dm³";
    public static final String DOSE_UNIT = "kg/ha";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TABELA", nullable = false)
    SoilFertilityInterpretationCriteriaTableModel table;

    @Column(name = "CTC_MENOR_40_TEOR_BAIXO_MENOR_QUE", nullable = false)
    Double lessThan40LowContentLessThan;
    @Column(name = "CTC_MENOR_40_DOSE_PARA_TEOR_BAIXO", nullable = false)
    Double lessThan40DoseForLowContent;
    @Column(name = "CTC_MENOR_40_MEDIO_MENOR_TEOR", nullable = false)
    Double lessThan40MediumLowerContent;
    @Column(name = "CTC_MENOR_40_MEDIO_MAIOR_TEOR", nullable = false)
    Double lessThan40MediumHigherContent;
    @Column(name = "CTC_MENOR_40_DOSE_PARA_TEOR_MEDIO", nullable = false)
    Double lessThan40DoseForMediumContent;
    @Column(name = "CTC_MENOR_40_ADEQUADO_MENOR_TEOR", nullable = false)
    Double lessThan40AdequateLowerContent;
    @Column(name = "CTC_MENOR_40_ADEQUADO_MAIOR_TEOR", nullable = false)
    Double lessThan40AdequateHigherContent;
    @Column(name = "CTC_MENOR_40_DOSE_PARA_TEOR_ADEQUADO", nullable = false)
    Double lessThan40DoseForAdequateContent;
    @Column(name = "CTC_MENOR_40_TEOR_ALTO_MAIOR_QUE", nullable = false)
    Double lessThan40HighContentGreaterThan;
    @Column(name = "CTC_MENOR_40_DOSE_PARA_TEOR_ALTO", nullable = false)
    Double lessThan40DoseForHighContent;

    @Column(name = "CTC_MAIOR_IGUAL_40_TEOR_BAIXO_MENOR_QUE", nullable = false)
    Double greaterOrEqual40LowContentLessThan;
    @Column(name = "CTC_MAIOR_IGUAL_40_DOSE_PARA_TEOR_BAIXO", nullable = false)
    Double greaterOrEqual40DoseForLowContent;
    @Column(name = "CTC_MAIOR_IGUAL_40_MEDIO_MENOR_TEOR", nullable = false)
    Double greaterOrEqual40MediumLowerContent;
    @Column(name = "CTC_MAIOR_IGUAL_40_MEDIO_MAIOR_TEOR", nullable = false)
    Double greaterOrEqual40MediumHigherContent;
    @Column(name = "CTC_MAIOR_IGUAL_40_DOSE_PARA_TEOR_MEDIO", nullable = false)
    Double greaterOrEqual40DoseForMediumContent;
    @Column(name = "CTC_MAIOR_IGUAL_40_ADEQUADO_MENOR_TEOR", nullable = false)
    Double greaterOrEqual40AdequateLowerContent;
    @Column(name = "CTC_MAIOR_IGUAL_40_ADEQUADO_MAIOR_TEOR", nullable = false)
    Double greaterOrEqual40AdequateHigherContent;
    @Column(name = "CTC_MAIOR_IGUAL_40_DOSE_PARA_TEOR_ADEQUADO", nullable = false)
    Double greaterOrEqual40DoseForAdequateContent;
    @Column(name = "CTC_MAIOR_IGUAL_40_TEOR_ALTO_MAIOR_QUE", nullable = false)
    Double greaterOrEqual40HighContentGreaterThan;
    @Column(name = "CTC_MAIOR_IGUAL_40_DOSE_PARA_TEOR_ALTO", nullable = false)
    Double greaterOrEqual40DoseForHighContent;

    @Column(name = "OBSERVACOES", length = 1000)
    String observations;

    @Column(name = "FONTES", length = 1000)
    String sources;

    public KContentAndDoseResponseDto toDto() {
        return KContentAndDoseResponseDto.builder()
                .id(this.id)
                .tableId(this.table != null ? this.table.getId() : null)
                .displayName(DISPLAY_NAME)
                .contentUnit(CONTENT_UNIT)
                .doseUnit(DOSE_UNIT)
                .lessThan40Section(toLessThan40SectionDto())
                .greaterOrEqual40Section(toGreaterOrEqual40SectionDto())
                .observations(this.observations)
                .sources(this.sources)
                .build();
    }

    private KContentAndDoseSectionDto toLessThan40SectionDto() {
        return KContentAndDoseSectionDto.builder()
                .lowContentLessThan(this.lessThan40LowContentLessThan)
                .doseForLowContent(this.lessThan40DoseForLowContent)
                .mediumLowerContent(this.lessThan40MediumLowerContent)
                .mediumHigherContent(this.lessThan40MediumHigherContent)
                .doseForMediumContent(this.lessThan40DoseForMediumContent)
                .adequateLowerContent(this.lessThan40AdequateLowerContent)
                .adequateHigherContent(this.lessThan40AdequateHigherContent)
                .doseForAdequateContent(this.lessThan40DoseForAdequateContent)
                .highContentGreaterThan(this.lessThan40HighContentGreaterThan)
                .doseForHighContent(this.lessThan40DoseForHighContent)
                .build();
    }

    private KContentAndDoseSectionDto toGreaterOrEqual40SectionDto() {
        return KContentAndDoseSectionDto.builder()
                .lowContentLessThan(this.greaterOrEqual40LowContentLessThan)
                .doseForLowContent(this.greaterOrEqual40DoseForLowContent)
                .mediumLowerContent(this.greaterOrEqual40MediumLowerContent)
                .mediumHigherContent(this.greaterOrEqual40MediumHigherContent)
                .doseForMediumContent(this.greaterOrEqual40DoseForMediumContent)
                .adequateLowerContent(this.greaterOrEqual40AdequateLowerContent)
                .adequateHigherContent(this.greaterOrEqual40AdequateHigherContent)
                .doseForAdequateContent(this.greaterOrEqual40DoseForAdequateContent)
                .highContentGreaterThan(this.greaterOrEqual40HighContentGreaterThan)
                .doseForHighContent(this.greaterOrEqual40DoseForHighContent)
                .build();
    }
}
