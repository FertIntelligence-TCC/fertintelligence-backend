package com.migueltcc.fertintelligence.model.fertintelligence.soilFertilizerModels;

import com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.organicFertilizer.OrganicFertilizerResponseDto;
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

    @Column(name = "VALOR_FRETE_TONELADA", precision = 19, scale = 2)
    private BigDecimal valorFreteTonelada;

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

    @Column(name = "PORCENTAGEM_MATERIA_ORGANICA")
    private Double teorMateriaOrganicaPercentual;

    @Column(name = "TAXA_MINERALIZACAO_PRIMEIRO_ANO_PERCENTUAL")
    private Double taxaMineralizacaoPrimeiroAnoPercentual;

    @Column(name = "TAXA_MINERALIZACAO_SEGUNDO_ANO_PERCENTUAL")
    private Double taxaMineralizacaoSegundoAnoPercentual;

    @Column(name = "TAXA_MINERALIZACAO_TERCEIRO_ANO_PERCENTUAL")
    private Double taxaMineralizacaoTerceiroAnoPercentual;

    @Column(name = "TAXA_MINERALIZACAO_QUARTO_ANO_PERCENTUAL")
    private Double taxaMineralizacaoQuartoAnoPercentual;

    @Column(name = "ARSENIO_MG_KG")
    private Double arsenioMgKg;

    @Column(name = "CADMIO_MG_KG")
    private Double cadmioMgKg;

    @Column(name = "CROMIO_MG_KG")
    private Double cromioMgKg;

    @Column(name = "CHUMBO_MG_KG")
    private Double chumboMgKg;

    @Column(name = "MERCURIO_MG_KG")
    private Double mercurioMgKg;

    @Column(name = "NIQUEL_MG_KG")
    private Double niquelMgKg;

    @Column(name = "SELENIO_MG_KG")
    private Double selenioMgKg;

    public OrganicFertilizerResponseDto toDto() {
        return OrganicFertilizerResponseDto.builder()
                .id(this.id)
                .name(this.name)
                .c(this.C)
                .n(this.N)
                .p2o5(this.P2O5)
                .k2o(this.K2O)
                .ca(this.Ca != null ? this.Ca : 0.0)
                .mg(this.Mg != null ? this.Mg : 0.0)
                .s(this.S != null ? this.S : 0.0)
                .b(this.B != null ? this.B : 0.0)
                .cu(this.Cu != null ? this.Cu : 0.0)
                .fe(this.Fe != null ? this.Fe : 0.0)
                .mn(this.Mn != null ? this.Mn : 0.0)
                .mo(this.Mo != null ? this.Mo : 0.0)
                .zn(this.Zn != null ? this.Zn : 0.0)
                .teorUmidade(this.teorUmidade)
                .teorMateriaOrganicaPercentual(this.teorMateriaOrganicaPercentual)
                .teorCarbonoOrganicoPercentual(calcularTeorCarbonoOrganicoPercentual())
                .relacaoCarbonoNitrogenio(calcularRelacaoCarbonoNitrogenio())
                .taxaMineralizacaoPrimeiroAnoPercentual(this.taxaMineralizacaoPrimeiroAnoPercentual)
                .taxaMineralizacaoSegundoAnoPercentual(this.taxaMineralizacaoSegundoAnoPercentual)
                .taxaMineralizacaoTerceiroAnoPercentual(this.taxaMineralizacaoTerceiroAnoPercentual)
                .taxaMineralizacaoQuartoAnoPercentual(this.taxaMineralizacaoQuartoAnoPercentual)
                .arsenioMgKg(this.arsenioMgKg)
                .cadmioMgKg(this.cadmioMgKg)
                .cromioMgKg(this.cromioMgKg)
                .chumboMgKg(this.chumboMgKg)
                .mercurioMgKg(this.mercurioMgKg)
                .niquelMgKg(this.niquelMgKg)
                .selenioMgKg(this.selenioMgKg)
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
                .valorFreteTonelada(this.valorFreteTonelada)
                .nomeCriador(this.user != null ? this.user.getName() : null)
                .build();
    }

    public static Double calcularTeorCarbonoOrganicoPercentual(Double materiaOrganica) {
        if (materiaOrganica == null) {
            return null;
        }
        return BigDecimal.valueOf(materiaOrganica)
                .divide(new BigDecimal("1.724"), 12, java.math.RoundingMode.HALF_UP)
                .doubleValue();
    }

    private Double calcularTeorCarbonoOrganicoPercentual() {
        return calcularTeorCarbonoOrganicoPercentual(this.teorMateriaOrganicaPercentual);
    }

    private Double calcularRelacaoCarbonoNitrogenio() {
        if (this.teorMateriaOrganicaPercentual == null) {
            return null;
        }
        if (this.N == null || this.N == 0d) {
            return null;
        }
        return BigDecimal.valueOf(calcularTeorCarbonoOrganicoPercentual())
                .divide(BigDecimal.valueOf(this.N), 12, java.math.RoundingMode.HALF_UP)
                .doubleValue();
    }
}
