package com.migueltcc.fertintelligence.dto.property;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Alterado de @Getter/@Setter para @Data
@Builder
@NoArgsConstructor // Adicionado
@AllArgsConstructor // Adicionado
public class PropertyResponseDto {

    @Schema(example = "1")
    @JsonProperty("id")
    private Long id;

    @Schema(example = "Fazenda Sol Nascente")
    @JsonProperty("nome")
    private String nome;

    @Schema(example = "Rua das Flores, 123, Zona Rural")
    @JsonProperty("endereco")
    private String endereco;

    @Schema(example = "12.345.678/0001-99")
    @JsonProperty("cnpj")
    private String cnpj;

    @Schema(description = "Dados de localização da propriedade")
    @JsonProperty("localizacao")
    private LocalizacaoDto localizacao;

    @Schema(example = "42")
    @JsonProperty("owner_id")
    private Long ownerId;

    @Schema(example = "João da Silva")
    @JsonProperty("owner_nome")
    private String ownerNome;
}