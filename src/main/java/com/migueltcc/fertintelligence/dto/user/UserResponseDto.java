package com.migueltcc.fertintelligence.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.migueltcc.fertintelligence.composedAtributes.Cargo;
import com.migueltcc.fertintelligence.composedAtributes.Telefone;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UserResponseDto {

    @Schema(example = "404")
    @JsonProperty("id")
    private Long id;

    @Schema(example = "Miguel Macedo Ferreira")
    @JsonProperty("nome")
    private String nome;

    @Schema(example = "13600319442")
    @JsonProperty("cpf")
    private String cpf;

    @Schema(example = "miguel.ferreira@ccc.ufcg.edu.br")
    @JsonProperty("email")
    private String email;

    @JsonProperty("datanasc")
    private DataNascDto datanasc;

    @Schema(example = "MASCULINO")
    @JsonProperty("genero")
    private String genero;

    @Schema(example = "+55 (83) 99121-4231")
    @JsonProperty("telefone")
    private TelefoneDto telefone;

    @Schema(example = "GRADUACAO")
    @JsonProperty("formacao")
    private String formacao;

    @Schema(example = "Engenheiro de Software")
    @JsonProperty("profissao")
    private String profissao;

    @Schema(example = "SECRETARIO")
    @JsonProperty("cargo")
    private Cargo cargo;

}
