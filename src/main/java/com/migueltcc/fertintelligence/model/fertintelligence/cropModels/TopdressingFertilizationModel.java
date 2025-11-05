package com.migueltcc.fertintelligence.model.fertintelligence.cropModels;

import com.migueltcc.fertintelligence.composedAttributes.crop.Date;
import com.migueltcc.fertintelligence.dto.topDressingFertilization.TopDressingFertilizationResponseDto; // Import adicionado
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
@Table(name = "ADUBACAO_COBERTURA")
@EqualsAndHashCode
public class TopdressingFertilizationModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "CULTURA", nullable = false)
    CropModel crop;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "day", column = @Column(name = "DIA", nullable = false)),
            @AttributeOverride(name = "month", column = @Column(name = "MES", nullable = false)),
            @AttributeOverride(name = "year", column = @Column(name = "ANO", nullable = false))
    })
    Date date;

    @Column(name = "ORDEM", nullable = false)
    Integer order;

    @Column(name = "FORMULADO", nullable = true)
    Double formulated;

    @Column(name = "SULFATO_DE_AMONIO", nullable = true)
    Double ammonium_sulfate;

    @Column(name = "UREIA", nullable = true)
    Double urea;

    @Column(name = "CLORETO_DE_POTASSIO", nullable = true)
    Double potassium_chloride;

    @Column(name = "SUPERFOSFATO_TRIPLO", nullable = true)
    Double triple_superphosphate;

    @Column(name = "SUPERFOSFATO_SIMPLES", nullable = true)
    Double simple_superphosphate;

    @Column(name = "MONOAMONIO_FOSFATO", nullable = true)
    Double monoammonium_phosphate;

    // --- CÓDIGO ADICIONADO ABAIXO ---

    public TopDressingFertilizationResponseDto toDto() {
        return TopDressingFertilizationResponseDto.builder()
                .id(this.id)
                .crop_id(this.crop.getId())
                .date(copyDate(this.date))
                .order(this.order)
                .formulated(this.formulated)
                .ammonium_sulfate(this.ammonium_sulfate)
                .urea(this.urea)
                .potassium_chloride(this.potassium_chloride)
                .triple_superphosphate(this.triple_superphosphate)
                .simple_superphosphate(this.simple_superphosphate)
                .monoammonium_phosphate(this.monoammonium_phosphate)
                .build();
    }

    private Date copyDate(Date date) {
        if (date == null) {
            return null;
        }
        return new Date(date.getDay(), date.getMonth(), date.getYear());
    }
}