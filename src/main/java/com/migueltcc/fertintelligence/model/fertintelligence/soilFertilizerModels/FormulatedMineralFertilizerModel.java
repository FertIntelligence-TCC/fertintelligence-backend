package com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels;

import com.migueltcc.fertintelligence.composedAttributes.fertilizers.Formulate;
import com.migueltcc.fertintelligence.composedAttributes.fertilizers.NPKrelation;
import com.migueltcc.fertintelligence.dto.fertilizers.attributes.FormulateDto;
import com.migueltcc.fertintelligence.dto.fertilizers.attributes.NPKrelationDto;
import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.formulatedMineralFertilizer.FormulatedMineralFertilizerResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import jakarta.persistence.*;
import lombok.*;

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
                .relation(toRelationDto(this.relation))
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
                .nomeCriador(this.user != null ? this.user.getName() : null)
                .build();
    }

    private static NPKrelationDto toRelationDto(NPKrelation relation) {
        if (relation == null) {
            return null;
        }

        double min = minPositive(relation.getN(), relation.getP(), relation.getK());
        if (min == 0.0) {
            return new NPKrelationDto(0.0, 0.0, 0.0);
        }

        return new NPKrelationDto(
                roundToTwoDecimalPlaces(relation.getN() / min),
                roundToTwoDecimalPlaces(relation.getP() / min),
                roundToTwoDecimalPlaces(relation.getK() / min)
        );
    }

    private static double minPositive(double n, double p, double k) {
        double min = Double.MAX_VALUE;
        if (n > 0.0) min = Math.min(min, n);
        if (p > 0.0) min = Math.min(min, p);
        if (k > 0.0) min = Math.min(min, k);
        return min == Double.MAX_VALUE ? 0.0 : min;
    }

    private static double roundToTwoDecimalPlaces(double value) {
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .stripTrailingZeros()
                .doubleValue();
    }
}
