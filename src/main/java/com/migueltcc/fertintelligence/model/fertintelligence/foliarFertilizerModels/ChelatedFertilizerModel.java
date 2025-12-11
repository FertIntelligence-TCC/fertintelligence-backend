package com.migueltcc.fertintelligence.model.fertintelligence.foliarFertilizerModels;

import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.chelatedFertilizer.ChelatedFertilizerResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
@Table(name = "ADUBOS_QUELATADOS")
@EqualsAndHashCode
public class ChelatedFertilizerModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ID_CRIADOR", nullable = false)
    private UserModel user;

    @Column(name = "NOME_ADUBO")
    private String name;

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
