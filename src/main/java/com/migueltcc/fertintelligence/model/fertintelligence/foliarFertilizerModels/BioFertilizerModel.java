package com.migueltcc.fertintelligence.model.fertintelligence.foliarFertilizerModels;

import com.migueltcc.fertintelligence.dto.fertilizers.foliarFertilizers.bioFertilizer.BioFertilizerResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
@Table(name = "BIO_FERTILIZANTES")
public class BioFertilizerModel {

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

    @ElementCollection
    @CollectionTable(name = "BIO_FERTILIZANTES_FOTOS", joinColumns = @JoinColumn(name = "ADUBO_ID"))
    @OrderColumn(name = "ORDEM")
    @Column(name = "ID_FOTO", nullable = false)
    @Builder.Default
    private List<String> idsFotos = new ArrayList<>();

    @Column(name = "OBSERVACAO", columnDefinition = "TEXT")
    private String observation;

    @Column(name = "FONTE", columnDefinition = "TEXT")
    private String source;

    @Column(name = "NOME_ADUBO", nullable = false)
    private String name;

    @Column(name = "DENSIDADE_G_ML")
    private Double densidadeGml;

    @Column(name = "CONCENTRACAO_VOLUME_G_L")
    private Double concentracaoVolumeGl;

    @Column(name = "CONCENTRACAO_MASSA_G_KG")
    private Double concentracaoMassaGkg;

    @Column(name = "PROTEINAS_G_L")
    private Double proteinasGl;

    @Column(name = "AMINOACIDOS_G_L")
    private Double aminoacidosGl;

    @Column(name = "AMIDOS_G_L")
    private Double amidosGl;

    @Column(name = "ACUCARES_G_L")
    private Double acucaresGl;

    @Column(name = "COMPOSTOS_DIVERSOS_G_L")
    private Double compostosDiversosGl;

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

    public BioFertilizerResponseDto toDto() {
        return BioFertilizerResponseDto.builder()
                .id(this.id)
                .name(this.name)
                .densidadeGml(this.densidadeGml)
                .concentracaoVolumeGl(this.concentracaoVolumeGl)
                .concentracaoMassaGkg(this.concentracaoMassaGkg)
                .proteinasGl(this.proteinasGl)
                .aminoacidosGl(this.aminoacidosGl)
                .amidosGl(this.amidosGl)
                .acucaresGl(this.acucaresGl)
                .compostosDiversosGl(this.compostosDiversosGl)
                // Tratamento seguro de nulos (Null-Safe)
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
                .idsFotos(this.idsFotos)
                .observation(this.observation)
                .source(this.source)
                .nomeCriador(this.user != null ? this.user.getName() : null)
                .build();
    }
}
