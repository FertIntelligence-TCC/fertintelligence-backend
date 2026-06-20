package com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria;

import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.exchangeableSodium.ExchangeableSodiumResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.SoilFertilityInterpretationCriteriaTableModel;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
@Data
@Table(name = "SODIO_TROCAVEL")
public class ExchangeableSodiumModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne
    @JoinColumn(name = "ID_TABELA", nullable = false, unique = true)
    SoilFertilityInterpretationCriteriaTableModel table;

    @Column(name = "CTCLESSTHAN4_3_VERYLOWLESSTHAN")
    Double ctcLessThan4_3_veryLowLessThan;

    @Column(name = "CTCLESSTHAN4_3_LOWMIN")
    Double ctcLessThan4_3_lowMin;

    @Column(name = "CTCLESSTHAN4_3_LOWMAX")
    Double ctcLessThan4_3_lowMax;

    @Column(name = "CTCLESSTHAN4_3_MEDIUMMIN")
    Double ctcLessThan4_3_mediumMin;

    @Column(name = "CTCLESSTHAN4_3_MEDIUMMAX")
    Double ctcLessThan4_3_mediumMax;

    @Column(name = "CTCLESSTHAN4_3_HIGHMIN")
    Double ctcLessThan4_3_highMin;

    @Column(name = "CTCLESSTHAN4_3_HIGHMAX")
    Double ctcLessThan4_3_highMax;

    @Column(name = "CTCLESSTHAN4_3_VERYHIGHGREATERTHAN")
    Double ctcLessThan4_3_veryHighGreaterThan;

    @Column(name = "CTCFROM4_3TO8_6_VERYLOWLESSTHAN")
    Double ctcFrom4_3To8_6_veryLowLessThan;

    @Column(name = "CTCFROM4_3TO8_6_LOWMIN")
    Double ctcFrom4_3To8_6_lowMin;

    @Column(name = "CTCFROM4_3TO8_6_LOWMAX")
    Double ctcFrom4_3To8_6_lowMax;

    @Column(name = "CTCFROM4_3TO8_6_MEDIUMMIN")
    Double ctcFrom4_3To8_6_mediumMin;

    @Column(name = "CTCFROM4_3TO8_6_MEDIUMMAX")
    Double ctcFrom4_3To8_6_mediumMax;

    @Column(name = "CTCFROM4_3TO8_6_HIGHMIN")
    Double ctcFrom4_3To8_6_highMin;

    @Column(name = "CTCFROM4_3TO8_6_HIGHMAX")
    Double ctcFrom4_3To8_6_highMax;

    @Column(name = "CTCFROM4_3TO8_6_VERYHIGHGREATERTHAN")
    Double ctcFrom4_3To8_6_veryHighGreaterThan;

    @Column(name = "CTCFROM8_7TO15_0_VERYLOWLESSTHAN")
    Double ctcFrom8_7To15_0_veryLowLessThan;

    @Column(name = "CTCFROM8_7TO15_0_LOWMIN")
    Double ctcFrom8_7To15_0_lowMin;

    @Column(name = "CTCFROM8_7TO15_0_LOWMAX")
    Double ctcFrom8_7To15_0_lowMax;

    @Column(name = "CTCFROM8_7TO15_0_MEDIUMMIN")
    Double ctcFrom8_7To15_0_mediumMin;

    @Column(name = "CTCFROM8_7TO15_0_MEDIUMMAX")
    Double ctcFrom8_7To15_0_mediumMax;

    @Column(name = "CTCFROM8_7TO15_0_HIGHMIN")
    Double ctcFrom8_7To15_0_highMin;

    @Column(name = "CTCFROM8_7TO15_0_HIGHMAX")
    Double ctcFrom8_7To15_0_highMax;

    @Column(name = "CTCFROM8_7TO15_0_VERYHIGHGREATERTHAN")
    Double ctcFrom8_7To15_0_veryHighGreaterThan;

    @Column(name = "CTCGREATERTHAN15_VERYLOWLESSTHAN")
    Double ctcGreaterThan15_veryLowLessThan;

    @Column(name = "CTCGREATERTHAN15_LOWMIN")
    Double ctcGreaterThan15_lowMin;

    @Column(name = "CTCGREATERTHAN15_LOWMAX")
    Double ctcGreaterThan15_lowMax;

    @Column(name = "CTCGREATERTHAN15_MEDIUMMIN")
    Double ctcGreaterThan15_mediumMin;

    @Column(name = "CTCGREATERTHAN15_MEDIUMMAX")
    Double ctcGreaterThan15_mediumMax;

    @Column(name = "CTCGREATERTHAN15_HIGHMIN")
    Double ctcGreaterThan15_highMin;

    @Column(name = "CTCGREATERTHAN15_HIGHMAX")
    Double ctcGreaterThan15_highMax;

    @Column(name = "CTCGREATERTHAN15_VERYHIGHGREATERTHAN")
    Double ctcGreaterThan15_veryHighGreaterThan;

    public ExchangeableSodiumResponseDto toDto() {
        return ExchangeableSodiumResponseDto.builder()
                .id(this.id)
                .tableId(this.table != null ? this.table.getId() : null)
                .ctcLessThan4_3_veryLowLessThan(this.ctcLessThan4_3_veryLowLessThan)
                .ctcLessThan4_3_lowMin(this.ctcLessThan4_3_lowMin)
                .ctcLessThan4_3_lowMax(this.ctcLessThan4_3_lowMax)
                .ctcLessThan4_3_mediumMin(this.ctcLessThan4_3_mediumMin)
                .ctcLessThan4_3_mediumMax(this.ctcLessThan4_3_mediumMax)
                .ctcLessThan4_3_highMin(this.ctcLessThan4_3_highMin)
                .ctcLessThan4_3_highMax(this.ctcLessThan4_3_highMax)
                .ctcLessThan4_3_veryHighGreaterThan(this.ctcLessThan4_3_veryHighGreaterThan)
                .ctcFrom4_3To8_6_veryLowLessThan(this.ctcFrom4_3To8_6_veryLowLessThan)
                .ctcFrom4_3To8_6_lowMin(this.ctcFrom4_3To8_6_lowMin)
                .ctcFrom4_3To8_6_lowMax(this.ctcFrom4_3To8_6_lowMax)
                .ctcFrom4_3To8_6_mediumMin(this.ctcFrom4_3To8_6_mediumMin)
                .ctcFrom4_3To8_6_mediumMax(this.ctcFrom4_3To8_6_mediumMax)
                .ctcFrom4_3To8_6_highMin(this.ctcFrom4_3To8_6_highMin)
                .ctcFrom4_3To8_6_highMax(this.ctcFrom4_3To8_6_highMax)
                .ctcFrom4_3To8_6_veryHighGreaterThan(this.ctcFrom4_3To8_6_veryHighGreaterThan)
                .ctcFrom8_7To15_0_veryLowLessThan(this.ctcFrom8_7To15_0_veryLowLessThan)
                .ctcFrom8_7To15_0_lowMin(this.ctcFrom8_7To15_0_lowMin)
                .ctcFrom8_7To15_0_lowMax(this.ctcFrom8_7To15_0_lowMax)
                .ctcFrom8_7To15_0_mediumMin(this.ctcFrom8_7To15_0_mediumMin)
                .ctcFrom8_7To15_0_mediumMax(this.ctcFrom8_7To15_0_mediumMax)
                .ctcFrom8_7To15_0_highMin(this.ctcFrom8_7To15_0_highMin)
                .ctcFrom8_7To15_0_highMax(this.ctcFrom8_7To15_0_highMax)
                .ctcFrom8_7To15_0_veryHighGreaterThan(this.ctcFrom8_7To15_0_veryHighGreaterThan)
                .ctcGreaterThan15_veryLowLessThan(this.ctcGreaterThan15_veryLowLessThan)
                .ctcGreaterThan15_lowMin(this.ctcGreaterThan15_lowMin)
                .ctcGreaterThan15_lowMax(this.ctcGreaterThan15_lowMax)
                .ctcGreaterThan15_mediumMin(this.ctcGreaterThan15_mediumMin)
                .ctcGreaterThan15_mediumMax(this.ctcGreaterThan15_mediumMax)
                .ctcGreaterThan15_highMin(this.ctcGreaterThan15_highMin)
                .ctcGreaterThan15_highMax(this.ctcGreaterThan15_highMax)
                .ctcGreaterThan15_veryHighGreaterThan(this.ctcGreaterThan15_veryHighGreaterThan)
                .build();
    }
}
