package com.migueltcc.fertintelligence.dto.property;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyPostRequestDto {

    @Schema(defaultValue = "nome")
    @JsonProperty("novo_nome")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    private String nome;

    @Schema(defaultValue = "endereco")
    @JsonProperty("novo_endereco")
    @Size(max = 512, message = "Endereço deve ter no máximo 512 caracteres")
    private String endereco;

    @Schema(defaultValue = "cnpj")
    @JsonProperty("novo_cnpj")
    @Pattern(
            regexp = "^\\d{14}$",
            message = "CNPJ deve conter 14 dígitos numéricos"
    )
    private String cnpj;

    @Schema(defaultValue = "localizacao")
    @JsonProperty("nova_localizacao")
    @Valid
    private LocalizacaoDto localizacao;

    @Schema(defaultValue = "idfoto")
    @JsonProperty("novo_idfoto")
    private String idFoto;
}
