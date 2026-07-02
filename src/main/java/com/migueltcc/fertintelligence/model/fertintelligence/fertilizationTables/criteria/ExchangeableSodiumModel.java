package com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria;

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
@Table(name = "SODIO_TROCAVEL")
public class ExchangeableSodiumModel {

    public static final String DEFAULT_UNIT = "mmolc/dm³";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TABELA", nullable = false)
    private SoilFertilityInterpretationCriteriaTableModel table;

    @Column(name = "UNIDADE_SODIO", nullable = false)
    @Builder.Default
    private String sodiumUnit = DEFAULT_UNIT;

    @Column(name = "UNIDADE_CTC", nullable = false)
    @Builder.Default
    private String ctcUnit = DEFAULT_UNIT;

    @Column(name = "CTC_MENOR_4_3_VERYLOWLESSTHAN", nullable = false)
    private Double ctcLessThan43VeryLowLessThan;

    @Column(name = "CTC_MENOR_4_3_LOWMIN", nullable = false)
    private Double ctcLessThan43LowMin;

    @Column(name = "CTC_MENOR_4_3_LOWMAX", nullable = false)
    private Double ctcLessThan43LowMax;

    @Column(name = "CTC_MENOR_4_3_MEDIUMMIN", nullable = false)
    private Double ctcLessThan43MediumMin;

    @Column(name = "CTC_MENOR_4_3_MEDIUMMAX", nullable = false)
    private Double ctcLessThan43MediumMax;

    @Column(name = "CTC_MENOR_4_3_HIGHMIN", nullable = false)
    private Double ctcLessThan43HighMin;

    @Column(name = "CTC_MENOR_4_3_HIGHMAX", nullable = false)
    private Double ctcLessThan43HighMax;

    @Column(name = "CTC_MENOR_4_3_VERYHIGHGREATERTHAN", nullable = false)
    private Double ctcLessThan43VeryHighGreaterThan;

    @Column(name = "CTC_4_3_A_8_6_VERYLOWLESSTHAN", nullable = false)
    private Double ctcFrom43To86VeryLowLessThan;

    @Column(name = "CTC_4_3_A_8_6_LOWMIN", nullable = false)
    private Double ctcFrom43To86LowMin;

    @Column(name = "CTC_4_3_A_8_6_LOWMAX", nullable = false)
    private Double ctcFrom43To86LowMax;

    @Column(name = "CTC_4_3_A_8_6_MEDIUMMIN", nullable = false)
    private Double ctcFrom43To86MediumMin;

    @Column(name = "CTC_4_3_A_8_6_MEDIUMMAX", nullable = false)
    private Double ctcFrom43To86MediumMax;

    @Column(name = "CTC_4_3_A_8_6_HIGHMIN", nullable = false)
    private Double ctcFrom43To86HighMin;

    @Column(name = "CTC_4_3_A_8_6_HIGHMAX", nullable = false)
    private Double ctcFrom43To86HighMax;

    @Column(name = "CTC_4_3_A_8_6_VERYHIGHGREATERTHAN", nullable = false)
    private Double ctcFrom43To86VeryHighGreaterThan;

    @Column(name = "CTC_8_7_A_15_0_VERYLOWLESSTHAN", nullable = false)
    private Double ctcFrom87To150VeryLowLessThan;

    @Column(name = "CTC_8_7_A_15_0_LOWMIN", nullable = false)
    private Double ctcFrom87To150LowMin;

    @Column(name = "CTC_8_7_A_15_0_LOWMAX", nullable = false)
    private Double ctcFrom87To150LowMax;

    @Column(name = "CTC_8_7_A_15_0_MEDIUMMIN", nullable = false)
    private Double ctcFrom87To150MediumMin;

    @Column(name = "CTC_8_7_A_15_0_MEDIUMMAX", nullable = false)
    private Double ctcFrom87To150MediumMax;

    @Column(name = "CTC_8_7_A_15_0_HIGHMIN", nullable = false)
    private Double ctcFrom87To150HighMin;

    @Column(name = "CTC_8_7_A_15_0_HIGHMAX", nullable = false)
    private Double ctcFrom87To150HighMax;

    @Column(name = "CTC_8_7_A_15_0_VERYHIGHGREATERTHAN", nullable = false)
    private Double ctcFrom87To150VeryHighGreaterThan;

    @Column(name = "CTC_MAIOR_15_VERYLOWLESSTHAN", nullable = false)
    private Double ctcGreaterThan15VeryLowLessThan;

    @Column(name = "CTC_MAIOR_15_LOWMIN", nullable = false)
    private Double ctcGreaterThan15LowMin;

    @Column(name = "CTC_MAIOR_15_LOWMAX", nullable = false)
    private Double ctcGreaterThan15LowMax;

    @Column(name = "CTC_MAIOR_15_MEDIUMMIN", nullable = false)
    private Double ctcGreaterThan15MediumMin;

    @Column(name = "CTC_MAIOR_15_MEDIUMMAX", nullable = false)
    private Double ctcGreaterThan15MediumMax;

    @Column(name = "CTC_MAIOR_15_HIGHMIN", nullable = false)
    private Double ctcGreaterThan15HighMin;

    @Column(name = "CTC_MAIOR_15_HIGHMAX", nullable = false)
    private Double ctcGreaterThan15HighMax;

    @Column(name = "CTC_MAIOR_15_VERYHIGHGREATERTHAN", nullable = false)
    private Double ctcGreaterThan15VeryHighGreaterThan;

    @Column(name = "OBSERVACOES", length = 1000)
    private String observations;

    @Column(name = "FONTES", length = 1000)
    private String sources;

    @PrePersist
    @PreUpdate
    private void normalizeUnits() {
        this.sodiumUnit = DEFAULT_UNIT;
        this.ctcUnit = DEFAULT_UNIT;
    }

}
