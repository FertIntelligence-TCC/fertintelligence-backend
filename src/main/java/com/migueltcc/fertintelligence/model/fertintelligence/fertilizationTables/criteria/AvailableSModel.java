package com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables.criteria;

import com.migueltcc.fertintelligence.dto.tables.soilFertilityInterpretationCriteria.availableS.AvailableSResponseDto;
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
@Table(name = "ENXOFRE_DISPONIVEL_NO_SOLO")
public class AvailableSModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne
    @JoinColumn(name = "ID_TABELA", nullable = false)
    SoilFertilityInterpretationCriteriaTableModel table;


    @Column(name = "MENOR_TEOR_ENXOFRE_ARGILA_MENOR_400", nullable = false)
    Double sContentLess400TooLow;
    @Column(name = "TEOR_INICIAL_BAIXO_ENXOFRE_ARGILA_MENOR_400", nullable = false)
    Double sContentLess400LowI;
    @Column(name = "TEOR_FINAL_BAIXO_ENXOFRE_ARGILA_MENOR_400", nullable = false)
    Double sContentLess400LowF;
    @Column(name = "TEOR_INICIAL_MEDIO_ENXOFRE_ARGILA_MENOR_400", nullable = false)
    Double sContentLess400MediumI;
    @Column(name = "TEOR_FINAL_MEDIO_ENXOFRE_ARGILA_MENOR_400", nullable = false)
    Double sContentLess400MediumF;
    @Column(name = "TEOR_INICIAL_ALTO_ENXOFRE_ARGILA_MENOR_400", nullable = false)
    Double sContentLess400HighI;
    @Column(name = "TEOR_FINAL_ALTO_ENXOFRE_ARGILA_MENOR_400", nullable = false)
    Double sContentLess400HighF;
    @Column(name = "MAIOR_TEOR_ENXOFRE_ARGILA_MENOR_400", nullable = false)
    Double sContentLess400TooHigh;

    @Column(name = "MENOR_TEOR_ENXOFRE_ARGILA_MAIOR_400", nullable = false)
    Double sContentGreater400TooLow;
    @Column(name = "TEOR_INICIAL_BAIXO_ENXOFRE_ARGILA_MAIOR_400", nullable = false)
    Double sContentGreater400LowI;
    @Column(name = "TEOR_FINAL_BAIXO_ENXOFRE_ARGILA_MAIOR_400", nullable = false)
    Double sContentGreater400LowF;
    @Column(name = "TEOR_INICIAL_MEDIO_ENXOFRE_ARGILA_MAIOR_400", nullable = false)
    Double sContentGreater400MediumI;
    @Column(name = "TEOR_FINAL_MEDIO_ENXOFRE_ARGILA_MAIOR_400", nullable = false)
    Double sContentGreater400MediumF;
    @Column(name = "TEOR_INICIAL_ALTO_ENXOFRE_ARGILA_MAIOR_400", nullable = false)
    Double sContentGreater400HighI;
    @Column(name = "TEOR_FINAL_ALTO_ENXOFRE_ARGILA_MAIOR_400", nullable = false)
    Double sContentGreater400HighF;
    @Column(name = "MAIOR_TEOR_ENXOFRE_ARGILA_MAIOR_400", nullable = false)
    Double sContentGreater400TooHigh;

    @Column(name = "FONTE_LITERATURA")
    String literatureSource;

    @Column(name = "OBSERVACOES", length = 1000)
    String observations;


    public AvailableSResponseDto toDto() {
        return AvailableSResponseDto.builder()
                .id(this.id)
                .tableId(this.table != null ? this.table.getId() : null)

                .sContentLess400TooLow(this.sContentLess400TooLow)
                .sContentLess400LowI(this.sContentLess400LowI)
                .sContentLess400LowF(this.sContentLess400LowF)
                .sContentLess400MediumI(this.sContentLess400MediumI)
                .sContentLess400MediumF(this.sContentLess400MediumF)
                .sContentLess400HighI(this.sContentLess400HighI)
                .sContentLess400HighF(this.sContentLess400HighF)
                .sContentLess400TooHigh(this.sContentLess400TooHigh)
                .sContentGreater400TooLow(this.sContentGreater400TooLow)
                .sContentGreater400LowI(this.sContentGreater400LowI)
                .sContentGreater400LowF(this.sContentGreater400LowF)
                .sContentGreater400MediumI(this.sContentGreater400MediumI)
                .sContentGreater400MediumF(this.sContentGreater400MediumF)
                .sContentGreater400HighI(this.sContentGreater400HighI)
                .sContentGreater400HighF(this.sContentGreater400HighF)
                .sContentGreater400TooHigh(this.sContentGreater400TooHigh)
                .literatureSource(this.literatureSource)
                .observations(this.observations)

                .build();
    }
}
