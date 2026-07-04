package com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels;

import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.simpleMineralFertilizer.SimpleMineralFertilizerResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CRIADOR", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UserModel user;

    @Column(name = "PUBLICO", nullable = false)
    private Boolean publico = false;

    @Column(name = "OBSERVACAO", columnDefinition = "TEXT")
    private String observation;

    @Column(name = "FONTE", columnDefinition = "TEXT")
    private String source;

    @Column(name = "DATA_TOMADA_PRECO")
    private LocalDate dataTomadaPreco;

    @Column(name = "PRECO_SACO_5KG", precision = 19, scale = 2)
    private BigDecimal precoSaco5Kg;

    @Column(name = "PRECO_SACO_25KG", precision = 19, scale = 2)
    private BigDecimal precoSaco25Kg;

    @Column(name = "PRECO_SACO_50KG", precision = 19, scale = 2)
    private BigDecimal precoSaco50Kg;

    @Column(name = "PRECO_SACO_1000KG", precision = 19, scale = 2)
    private BigDecimal precoSaco1000Kg;

    @Column(name = "NOME_ADUBO")
    private String name;

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
                .name(this.name)
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
                .publico(this.publico != null ? this.publico : false)
                .idsFotos(List.of())
                .observation(this.observation)
                .source(this.source)
                .dataTomadaPreco(this.dataTomadaPreco)
                .precoSaco5Kg(this.precoSaco5Kg)
                .precoSaco25Kg(this.precoSaco25Kg)
                .precoSaco50Kg(this.precoSaco50Kg)
                .precoSaco1000Kg(this.precoSaco1000Kg)
                .nomeCriador(this.user != null ? this.user.getName() : null)
                .build();
    }
}
