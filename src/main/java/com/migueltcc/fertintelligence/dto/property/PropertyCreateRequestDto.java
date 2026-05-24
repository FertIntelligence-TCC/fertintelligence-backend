package com.migueltcc.fertintelligence.dto.property;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class PropertyCreateRequestDto {

    @JsonProperty("nome")
    @NotBlank(message = "Nome não pode ser vazio")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    private String nome;

    @JsonProperty("endereco")
    @NotBlank(message = "Endereço não pode ser vazio")
    @Size(max = 512, message = "Endereço deve ter no máximo 512 caracteres")
    private String endereco;

    @JsonProperty("cnpj")
    @NotBlank(message = "CNPJ não pode ser vazio")
    @Pattern(
            regexp = "^\\d{14}$",
            message = "CNPJ deve conter 14 dígitos numéricos"
    )
    private String cnpj;

    @JsonProperty("localizacao")
    @NotNull(message = "Localização é obrigatória")
    @Valid
    private LocalizacaoDto localizacao;

    @JsonProperty("idfoto")
    private String idFoto;
}
