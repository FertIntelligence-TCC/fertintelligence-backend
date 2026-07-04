package com.migueltcc.fertintelligence.model.fertintelligence.foliarFertilizerModels;

import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.chelatedFertilizer.ChelatedFertilizerResponseDto;
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
@Table(name = "ADUBOS_QUELATADOS")
public class ChelatedFertilizerModel {

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

    @Column(name = "NOME_ADUBO", nullable = false)
    private String name;

    @Column(name = "DENSIDADE_G_ML")
    private Double densidadeGml;

    @Column(name = "CONCENTRACAO_VOLUME_G_L")
    private Double concentracaoVolumeGl;

    @Column(name = "CONCENTRACAO_MASSA_G_KG")
    private Double concentracaoMassaGkg;

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

    // Índices Físico-Químicos
    @Column(name = "INDICE_SALINO")
    private Double indiceSalino;

    @Column(name = "INDICE_ACIDEZ")
    private Double indiceAcidez;

    public ChelatedFertilizerResponseDto toDto() {
        return ChelatedFertilizerResponseDto.builder()
                .id(this.id)
                .name(this.name)
                .densidadeGml(this.densidadeGml)
                .concentracaoVolumeGl(this.concentracaoVolumeGl)
                .concentracaoMassaGkg(this.concentracaoMassaGkg)
                // Tratamento seguro de nulos
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
                .indiceSalino(this.indiceSalino != null ? this.indiceSalino : 0.0)
                .indiceAcidez(this.indiceAcidez != null ? this.indiceAcidez : 0.0)
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
