package com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels;

import com.migueltcc.fertintelligence.composedAttributes.fertilizers.Formulate;
import com.migueltcc.fertintelligence.composedAttributes.fertilizers.NPKrelation;
import com.migueltcc.fertintelligence.dto.fertilizers.attributes.FormulateDto;
import com.migueltcc.fertintelligence.dto.fertilizers.attributes.NPKrelationDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.formulatedMineralFertilizer.FormulatedMineralFertilizerResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import java.math.BigDecimal;
import java.math.RoundingMode;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
@Table(name = "ADUBOS_MINERAIS_FORMULADOS")
@EqualsAndHashCode
public class FormulatedMineralFertilizerModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ID_CRIADOR", nullable = false)
    private UserModel user;

    @Column(name = "PUBLICO", nullable = false)
    private Boolean publico = false;

    @ElementCollection
    @CollectionTable(name = "ADUBOS_MINERAIS_FORMULADOS_FOTOS", joinColumns = @JoinColumn(name = "ADUBO_ID"))
    @OrderColumn(name = "ORDEM")
    @Column(name = "ID_FOTO", nullable = false)
    @Builder.Default
    private List<String> idsFotos = new ArrayList<>();

    @Column(name = "OBSERVACAO", columnDefinition = "TEXT")
    private String observation;

    @Column(name = "FONTE", columnDefinition = "TEXT")
    private String source;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "n", column = @Column(name = "FORMULA_N", nullable = false)),
            @AttributeOverride(name = "p", column = @Column(name = "FORMULA_P2O5", nullable = false)),
            @AttributeOverride(name = "k", column = @Column(name = "FORMULA_K2O", nullable = false))
    })
    private Formulate formulate;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "n", column = @Column(name = "RELACAO_N", nullable = false)),
            @AttributeOverride(name = "p", column = @Column(name = "RELACAO_P2O5", nullable = false)),
            @AttributeOverride(name = "k", column = @Column(name = "RELACAO_K2O", nullable = false))
    })
    private NPKrelation relation;

    // Macronutrientes Primários (Garantias)
    @Column(name = "PORCENTAGEM_NITROGENIO")
    private double N;

    @Column(name = "PORCENTAGEM_P2O5")
    private double P2O5;

    @Column(name = "PORCENTAGEM_K2O")
    private double K2O;

    // Macronutrientes Secundários
    @Column(name = "PORCENTAGEM_CALCIO")
    private Double Ca;

    @Column(name = "PORCENTAGEM_MAGNESIO")
    private Double Mg;

    @Column(name = "PORCENTAGEM_ENXOFRE")
    private Double S;

    // Micronutrientes
    @Column(name = "PORCENTAGEM_BORO")
    private Double B;

    @Column(name = "PORCENTAGEM_COBRE")
    private Double Cu;

    @Column(name = "PORCENTAGEM_FERRO")
    private Double Fe;

    @Column(name = "PORCENTAGEM_MANGANES")
    private Double Mn;

    @Column(name = "PORCENTAGEM_MOLIBDENIO")
    private Double Mo;

    @Column(name = "PORCENTAGEM_ZINCO")
    private Double Zn;

    // Identificação da Fórmula
    @Column(name = "NUMERO_FORMULA_INDICADA")
    private Integer indicatedFormulaNumber;

    public FormulatedMineralFertilizerResponseDto toDto() {
        return FormulatedMineralFertilizerResponseDto.builder()
                .id(this.id)
                .formulate(this.formulate != null ?
                        new FormulateDto(this.formulate.getN(), this.formulate.getP(), this.formulate.getK()) : null)
                .relation(toRelationDto(this.formulate, this.relation))
                .n(this.N)
                .p2o5(this.P2O5)
                .k2o(this.K2O)
                .ca(this.Ca)
                .mg(this.Mg)
                .s(this.S)
                .b(this.B)
                .cu(this.Cu)
                .fe(this.Fe)
                .mn(this.Mn)
                .mo(this.Mo)
                .zn(this.Zn)
                .indicatedFormulaNumber(this.indicatedFormulaNumber)
                .userId(this.user != null ? this.user.getId() : null)
                .userNome(this.user != null ? this.user.getName() : null)
                .publico(this.publico != null ? this.publico : false)
                .idsFotos(this.idsFotos)
                .observation(this.observation)
                .source(this.source)
                .nomeCriador(this.user != null ? this.user.getName() : null)
                .build();
    }

    public static NPKrelation calculateRelation(Formulate formulate) {
        if (formulate == null) {
            return new NPKrelation(0.0, 0.0, 0.0);
        }

        int minimum = smallestPositiveValue(formulate);
        if (minimum == 0) {
            return new NPKrelation(0.0, 0.0, 0.0);
        }

        return new NPKrelation(
                divideByMinimum(formulate.getN(), minimum),
                divideByMinimum(formulate.getP(), minimum),
                divideByMinimum(formulate.getK(), minimum)
        );
    }

    private static NPKrelationDto toRelationDto(Formulate formulate, NPKrelation relation) {
        NPKrelation calculated = formulate != null ? calculateRelation(formulate) : relation;
        if (calculated == null) {
            return null;
        }

        return new NPKrelationDto(
                roundToTwoDecimalPlaces(calculated.getN()),
                roundToTwoDecimalPlaces(calculated.getP()),
                roundToTwoDecimalPlaces(calculated.getK())
        );
    }

    private static int smallestPositiveValue(Formulate formulate) {
        int minimum = Integer.MAX_VALUE;
        if (formulate.getN() > 0) minimum = Math.min(minimum, formulate.getN());
        if (formulate.getP() > 0) minimum = Math.min(minimum, formulate.getP());
        if (formulate.getK() > 0) minimum = Math.min(minimum, formulate.getK());
        return minimum == Integer.MAX_VALUE ? 0 : minimum;
    }

    private static double divideByMinimum(int value, int minimum) {
        return roundToTwoDecimalPlaces((double) value / minimum);
    }

    private static double roundToTwoDecimalPlaces(double value) {
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .stripTrailingZeros()
                .doubleValue();
    }
}
