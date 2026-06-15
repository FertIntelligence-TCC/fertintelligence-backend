package com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels;

import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.organicFertilizer.OrganicFertilizerResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
@Table(name = "ADUBOS_ORGANICOS")
public class OrganicFertilizerModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CRIADOR", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UserModel user;

    @Column(name = "PUBLICO", nullable = false)
    private Boolean publico = false;

    @Column(name = "NOME_ADUBO", nullable = false)
    private String name;

    // Componente Orgânico
    @Column(name = "PORCENTAGEM_CARBONO")
    private Double C;

    // Macronutrientes Primários
    @Column(name = "PORCENTAGEM_NITROGENIO")
    private Double N;

    @Column(name = "PORCENTAGEM_P2O5")
    private Double P2O5;

    @Column(name = "PORCENTAGEM_K2O")
    private Double K2O;

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

    // Índices de Composição Física
    @Column(name = "PORCENTAGEM_UMIDADE")
    private Double teorUmidade;

    @Column(name = "PORCENTAGEM_CINZAS")
    private Double teorCinzas;

    public OrganicFertilizerResponseDto toDto() {
        return OrganicFertilizerResponseDto.builder()
                .id(this.id)
                .name(this.name)
                .c(this.C != null ? this.C : 0.0)
                .n(this.N != null ? this.N : 0.0)
                .p2o5(this.P2O5 != null ? this.P2O5 : 0.0)
                .k2o(this.K2O != null ? this.K2O : 0.0)
                .ca(this.Ca != null ? this.Ca : 0.0)
                .mg(this.Mg != null ? this.Mg : 0.0)
                .s(this.S != null ? this.S : 0.0)
                .b(this.B != null ? this.B : 0.0)
                .cu(this.Cu != null ? this.Cu : 0.0)
                .fe(this.Fe != null ? this.Fe : 0.0)
                .mn(this.Mn != null ? this.Mn : 0.0)
                .mo(this.Mo != null ? this.Mo : 0.0)
                .zn(this.Zn != null ? this.Zn : 0.0)
                .teorUmidade(this.teorUmidade != null ? this.teorUmidade : 0.0)
                .teorCinzas(this.teorCinzas != null ? this.teorCinzas : 0.0)
                .build();
    }
}
