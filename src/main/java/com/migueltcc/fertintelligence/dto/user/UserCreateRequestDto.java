package com.migueltcc.fertintelligence.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.migueltcc.fertintelligence.composedAttributes.user.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequestDto {

    @JsonProperty("username")
    @NotBlank
    private String username;

    @JsonProperty("cpf")
    @NotBlank
    private String cpf;

    @JsonProperty("email")
    @NotBlank
    private String email;

    @JsonProperty("datanasc")
    @NotNull
    private DataNasc datanasc;

    @JsonProperty("genero")
    @NotNull
    private Genero genero;

    @JsonProperty("telefone")
    @NotNull
    private Telefone telefone;

    @JsonProperty("formacao")
    @NotNull
    private Formacao formacao;

    @JsonProperty("profissao")
    @NotBlank
    private String profissao;

    @JsonProperty("cargo")
    @NotNull
    private Cargo cargo;

    @JsonProperty("senha")
    @NotBlank
    private String password;

    @JsonProperty("name")
    @NotBlank
    private String name;

    @JsonProperty("idfoto")
    private String idfoto;

}