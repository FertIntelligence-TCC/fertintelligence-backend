package com.migueltcc.fertintelligence.model.fertintelligence.fertilizationTables;

import com.migueltcc.fertintelligence.composedAttributes.fertilizationTables.*;
import com.migueltcc.fertintelligence.dto.tables.cropFertilization.CropFertilizationTableResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
@Data
@Table(name = "TABELAS_ADUBACAO_CULTURAS")
public class CropFertilizationTableModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "ID_CRIADOR", nullable = false)
    private UserModel creator;

    @Column(name = "REGIOES_CULTURA", nullable = false)
    private Regiao region;

    @Column(name = "NOME_COMUM_CULTURA", nullable = false)
    private NomeComum crop_common_name;

    @Column(name = "NOME_CIENTIFICO_CULTURA", nullable = false)
    private NomeCientifico crop_scientific_nome; // Deve corresponder ao campo acima

    @Column(name = "CULTIVARES", nullable = false)
    private String cultivares;

    @Column(name = "ESPACAMENTOS_SUGERIDOS", nullable = false)
    private SpacingType suggested_spacing;

    @Column(name = "VALOR_INICIAL", nullable = false)
    private Double initial_value;

    @Column(name = "VALOR_FINAL", nullable = false)
    private Double final_value;

    @Column(name = "ESPACAMENTO_USADO", nullable = false)
    private SpacingType used_spacing;

    @Column(name = "VALOR_ESPACAMENTO_USADO", nullable = false)
    private Double used_spacing_value;

    @Column(name = "PRODUTIVIDADE_REGIONAL", nullable = false)
    private Double regional_productivity; // kg/ha

    @Column(name = "PRODUTIVIDADE_ESPERADA", nullable = false)
    private Double expected_productivity; // kg/ha

    @Column(name = "CRITERIO_DE_CALAGEM", nullable = false)
    private CriterioCalagem criteria;

    // @Column(name = "NECESSIDADE_CALAGEM", nullable = false)
    // private Double liming_necessity; // t/ha

    @Column(name = "TIPO_DE_ESTERCO", nullable = false)
    private TipoEsterco manure;

    @Column(name = "QUANTIDADE_DE_ESTERCO", nullable = false)
    private Double manure_qtd; // t/ha

    @Column(name = "SUGESTAO_GESSAGEM", nullable = false)
    private Double gessing; // t/ha

    @Column(name = "SUGESTAO_DE_ADUBACAO_COM_MICRONUTRIENTES", nullable = false)
    private Double micronutrients; // g/ha

    @Column(name = "SUGESTAO_DE_ADUBACAO_COM_N_P2O5_K2O", nullable = false)
    private Double npk; // kg/ha

    @Column(name = "OBSERVACOES")
    private String observations;

    @Column(name = "FONTES")
    private String sources;

    @Column(name = "TABELA_PUBLICA", nullable = false)
    @Builder.Default
    private boolean publicTable = false;

    public CropFertilizationTableResponseDto toDto() {
        return CropFertilizationTableResponseDto.builder()
                .id(this.id)
                .creator_id(this.creator.getId())
                .creator_name(this.creator.getName())
                .region(this.region)
                .crop_common_name(this.crop_common_name)
                .crop_scientific_nome(this.crop_scientific_nome)
                .cultivares(this.cultivares)
                .suggested_spacing(this.suggested_spacing)
                .initial_value(this.initial_value)
                .final_value(this.final_value)
                .used_spacing(this.used_spacing)
                .used_spacing_value(this.used_spacing_value)
                .regional_productivity(this.regional_productivity)
                .expected_productivity(this.expected_productivity)
                .criteria(this.criteria)
                .manure(this.manure)
                .manure_qtd(this.manure_qtd)
                .gessing(this.gessing)
                .micronutrients(this.micronutrients)
                .npk(this.npk)
                .observations(this.observations)
                .sources(this.sources)
                .public_table(this.publicTable)
                .build();
    }

}
