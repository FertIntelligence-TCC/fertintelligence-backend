package com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria;

import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.exchangeableBaseRatio.ExchangeableBaseRatioResponseDto;
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
@Table(name = "RELACOES_ENTRE_BASES_TROCAVEIS")
public class ExchangeableBaseRatioModel {

    public static final String DISPLAY_NAME = "Relações entre bases trocáveis";
    public static final String DEFAULT_UNIT = "adimensional";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TABELA", nullable = false)
    SoilFertilityInterpretationCriteriaTableModel table;

    @Column(name = "UNIDADE", nullable = false)
    @Builder.Default
    String unit = DEFAULT_UNIT;

    @Column(name = "CA_MG_BAIXO", nullable = false)
    Double caMgLow;
    @Column(name = "CA_MG_MEDIO_MENOR_RELACAO", nullable = false)
    Double caMgMediumLowerRatio;
    @Column(name = "CA_MG_MEDIO_MAIOR_RELACAO", nullable = false)
    Double caMgMediumHigherRatio;
    @Column(name = "CA_MG_ADEQUADO_MENOR_RELACAO", nullable = false)
    Double caMgAdequateLowerRatio;
    @Column(name = "CA_MG_ADEQUADO_MAIOR_RELACAO", nullable = false)
    Double caMgAdequateHigherRatio;
    @Column(name = "CA_MG_ALTO", nullable = false)
    Double caMgHigh;

    @Column(name = "CA_K_BAIXO", nullable = false)
    Double caKLow;
    @Column(name = "CA_K_MEDIO_MENOR_RELACAO", nullable = false)
    Double caKMediumLowerRatio;
    @Column(name = "CA_K_MEDIO_MAIOR_RELACAO", nullable = false)
    Double caKMediumHigherRatio;
    @Column(name = "CA_K_ADEQUADO_MENOR_RELACAO", nullable = false)
    Double caKAdequateLowerRatio;
    @Column(name = "CA_K_ADEQUADO_MAIOR_RELACAO", nullable = false)
    Double caKAdequateHigherRatio;
    @Column(name = "CA_K_ALTO", nullable = false)
    Double caKHigh;

    @Column(name = "MG_K_BAIXO", nullable = false)
    Double mgKLow;
    @Column(name = "MG_K_MEDIO_MENOR_RELACAO", nullable = false)
    Double mgKMediumLowerRatio;
    @Column(name = "MG_K_MEDIO_MAIOR_RELACAO", nullable = false)
    Double mgKMediumHigherRatio;
    @Column(name = "MG_K_ADEQUADO_MENOR_RELACAO", nullable = false)
    Double mgKAdequateLowerRatio;
    @Column(name = "MG_K_ADEQUADO_MAIOR_RELACAO", nullable = false)
    Double mgKAdequateHigherRatio;
    @Column(name = "MG_K_ALTO", nullable = false)
    Double mgKHigh;

    @Column(name = "CA_MG_K_BAIXO", nullable = false)
    Double caMgKLow;
    @Column(name = "CA_MG_K_MEDIO_MENOR_RELACAO", nullable = false)
    Double caMgKMediumLowerRatio;
    @Column(name = "CA_MG_K_MEDIO_MAIOR_RELACAO", nullable = false)
    Double caMgKMediumHigherRatio;
    @Column(name = "CA_MG_K_ADEQUADO_MENOR_RELACAO", nullable = false)
    Double caMgKAdequateLowerRatio;
    @Column(name = "CA_MG_K_ADEQUADO_MAIOR_RELACAO", nullable = false)
    Double caMgKAdequateHigherRatio;
    @Column(name = "CA_MG_K_ALTO", nullable = false)
    Double caMgKHigh;

    @Column(name = "OBSERVACOES", length = 1000)
    String observations;

    @Column(name = "FONTES", length = 1000)
    String sources;

    @PrePersist
    @PreUpdate
    private void normalizeUnit() {
        this.unit = DEFAULT_UNIT;
    }

    public ExchangeableBaseRatioResponseDto toDto() {
        return ExchangeableBaseRatioResponseDto.builder()
                .id(this.id)
                .tableId(this.table != null ? this.table.getId() : null)
                .displayName(DISPLAY_NAME)
                .unit(DEFAULT_UNIT)
                .caMgLow(this.caMgLow)
                .caMgMediumLowerRatio(this.caMgMediumLowerRatio)
                .caMgMediumHigherRatio(this.caMgMediumHigherRatio)
                .caMgAdequateLowerRatio(this.caMgAdequateLowerRatio)
                .caMgAdequateHigherRatio(this.caMgAdequateHigherRatio)
                .caMgHigh(this.caMgHigh)
                .caKLow(this.caKLow)
                .caKMediumLowerRatio(this.caKMediumLowerRatio)
                .caKMediumHigherRatio(this.caKMediumHigherRatio)
                .caKAdequateLowerRatio(this.caKAdequateLowerRatio)
                .caKAdequateHigherRatio(this.caKAdequateHigherRatio)
                .caKHigh(this.caKHigh)
                .mgKLow(this.mgKLow)
                .mgKMediumLowerRatio(this.mgKMediumLowerRatio)
                .mgKMediumHigherRatio(this.mgKMediumHigherRatio)
                .mgKAdequateLowerRatio(this.mgKAdequateLowerRatio)
                .mgKAdequateHigherRatio(this.mgKAdequateHigherRatio)
                .mgKHigh(this.mgKHigh)
                .caMgKLow(this.caMgKLow)
                .caMgKMediumLowerRatio(this.caMgKMediumLowerRatio)
                .caMgKMediumHigherRatio(this.caMgKMediumHigherRatio)
                .caMgKAdequateLowerRatio(this.caMgKAdequateLowerRatio)
                .caMgKAdequateHigherRatio(this.caMgKAdequateHigherRatio)
                .caMgKHigh(this.caMgKHigh)
                .observations(this.observations)
                .sources(this.sources)
                .build();
    }
}
