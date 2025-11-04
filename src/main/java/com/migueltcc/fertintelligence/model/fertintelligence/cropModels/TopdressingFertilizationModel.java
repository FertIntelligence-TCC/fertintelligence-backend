package com.migueltcc.fertintelligence.model.fertintelligence.cropModels;

import com.migueltcc.fertintelligence.composedAttributes.Crop.Date;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

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

    @Column(name = "DATA", nullable = false)
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

}
