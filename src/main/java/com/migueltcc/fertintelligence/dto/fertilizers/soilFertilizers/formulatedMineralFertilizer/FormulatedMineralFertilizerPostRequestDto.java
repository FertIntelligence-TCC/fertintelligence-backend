package com.migueltcc.fertintelligence.dto.fertilizers.soilFertilizers.formulatedMineralFertilizer;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.migueltcc.fertintelligence.dto.fertilizers.attributes.FormulateDto;
import com.migueltcc.fertintelligence.dto.fertilizers.attributes.NPKrelationDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormulatedMineralFertilizerPostRequestDto {

    @JsonProperty("nova_formula")
    @JsonAlias("novo_formulate")
    @Valid
    private FormulateDto formulate;

    @JsonProperty("nova_relacao")
    @JsonAlias("novo_relation")
    @Valid
    private NPKrelationDto relation;

    @JsonProperty("novo_numero_formula_indicada")
    private Integer indicatedFormulaNumber;

    // Garantias
    @JsonProperty("novo_n")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double n;

    @JsonProperty("novo_p2o5")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double p2o5;

    @JsonProperty("novo_k2o")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double k2o;

    // Secundários
    @JsonProperty("novo_ca")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double ca;

    @JsonProperty("novo_mg")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double mg;

    @JsonProperty("novo_s")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double s;

    // Micronutrientes
    @JsonProperty("novo_b")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double b;

    @JsonProperty("novo_cu")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double cu;

    @JsonProperty("novo_fe")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double fe;

    @JsonProperty("novo_mn")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double mn;

    @JsonProperty("novo_mo")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double mo;

    @JsonProperty("novo_zn")
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private Double zn;

    @JsonProperty("data_tomada_preco")
    @JsonAlias({"dataTomadaPreco", "nova_data_tomada_preco"})
    private LocalDate dataTomadaPreco;

    @JsonProperty("preco_saco_5kg")
    @JsonAlias({"precoSaco5Kg", "preco_saco_5_kg", "novo_preco_saco_5kg", "novo_preco_saco_5_kg"})
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private BigDecimal precoSaco5Kg;

    @JsonProperty("preco_saco_25kg")
    @JsonAlias({"precoSaco25Kg", "preco_saco_25_kg", "novo_preco_saco_25kg", "novo_preco_saco_25_kg"})
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private BigDecimal precoSaco25Kg;

    @JsonProperty("preco_saco_50kg")
    @JsonAlias({"precoSaco50Kg", "preco_saco_50_kg", "novo_preco_saco_50kg", "novo_preco_saco_50_kg"})
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private BigDecimal precoSaco50Kg;

    @JsonProperty("preco_saco_1000kg")
    @JsonAlias({"precoSaco1000Kg", "preco_saco_1000_kg", "novo_preco_saco_1000kg", "novo_preco_saco_1000_kg"})
    @DecimalMin(value = "0.0", message = "O valor não pode ser negativo")
    private BigDecimal precoSaco1000Kg;


    @Size(max = 5, message = "Um adubo pode ter no máximo 5 fotos")
    @JsonProperty("novos_ids_fotos")
    private List<String> idsFotos;

    @JsonProperty("novo_observacao")
    private String observation;

    @JsonProperty("novo_fonte")
    private String source;

    @JsonProperty("novo_publico")
    private Boolean novoPublico;
}
