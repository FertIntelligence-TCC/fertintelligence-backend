package com.migueltcc.fertintelligence.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.migueltcc.fertintelligence.composedAttributes.User.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPostRequestDto {

    @Schema(defaultValue = "name")
    @JsonProperty("novo_nome")
    private String nome;

    @Schema(defaultValue = "username")
    @JsonProperty("novo_username")
    private String username;

    @Schema(defaultValue = "cpf")
    @JsonProperty("novo_cpf")
    private String cpf;

    @Schema(defaultValue = "email")
    @JsonProperty("novo_email")
    private String email;

    @Schema(defaultValue = "datanasc")
    @JsonProperty("nova_datanasc")
    private DataNasc datanasc;

    @Schema(defaultValue = "genero")
    @JsonProperty("novo_genero")
    private Genero genero;

    @Schema(defaultValue = "telefone")
    @JsonProperty("novo_telefone")
    private Telefone telefone;

    @Schema(defaultValue = "formacao")
    @JsonProperty("nova_formacao")
    private Formacao formacao;

    @Schema(defaultValue = "genero")
    @JsonProperty("nova_profissao")
    private String profissao;

    @Schema(defaultValue = "cargo")
    @JsonProperty("novo_cargo")
    private Cargo cargo;

    @Schema(defaultValue = "senha")
    @JsonProperty("nova_senha")
    private String password;

}