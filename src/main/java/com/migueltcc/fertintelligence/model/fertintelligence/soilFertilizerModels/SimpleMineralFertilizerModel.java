package com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels;

import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.simpleMineralFertilizer.SimpleMineralFertilizerResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
@Table(name = "ADUBOS_MINERAIS_SIMPLES")
@EqualsAndHashCode
public class SimpleMineralFertilizerModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ID_CRIADOR", nullable = false)
    private UserModel user;

    // Macronutrientes Primários
    @Column(name = "PORCENTAGEM_NITROGENIO")
    private double N;

    @Column(name = "PORCENTAGEM_P2O5")
    private double P2O5;

    @Column(name = "PORCENTAGEM_K2O")
    private double K2O;

    // Macronutrientes Secundários
    @Column(name = "PORCENTAGEM_CALCIO")
    private double Ca;

    @Column(name = "PORCENTAGEM_MAGNESIO")
    private double Mg;

    @Column(name = "PORCENTAGEM_ENXOFRE")
    private double S;

    // Micronutrientes
    @Column(name = "PORCENTAGEM_BORO")
    private double B;

    @Column(name = "PORCENTAGEM_COBRE")
    private double Cu;

    @Column(name = "PORCENTAGEM_FERRO")
    private double Fe;

    @Column(name = "PORCENTAGEM_MANGANES")
    private double Mn;

    @Column(name = "PORCENTAGEM_MOLIBDENIO")
    private double Mo;

    @Column(name = "PORCENTAGEM_ZINCO")
    private double Zn;

    // Índices Físico-Químicos
    @Column(name = "INDICE_SALINO")
    private double indiceSalino;

    @Column(name = "INDICE_ACIDEZ")
    private double indiceAcidez;

    public SimpleMineralFertilizerResponseDto toDto() {
        return SimpleMineralFertilizerResponseDto.builder()
                .id(this.id)
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
                .indiceSalino(this.indiceSalino)
                .indiceAcidez(this.indiceAcidez)
                .userId(this.user != null ? this.user.getId() : null)
                .userNome(this.user != null ? this.user.getName() : null)
                .build();
    }
}