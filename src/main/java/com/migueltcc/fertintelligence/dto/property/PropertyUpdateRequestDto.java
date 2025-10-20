package com.migueltcc.fertintelligence.dto.property;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PropertyUpdateRequestDto {

    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    private String nome; // Opcional

    @Size(max = 512, message = "Endereço deve ter no máximo 512 caracteres")
    private String endereco; // Opcional

    @Pattern(regexp = "^\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}$", message = "CNPJ deve estar no formato XX.XXX.XXX/0001-XX")
    private String cnpj; // Opcional

    @Valid
    private LocalizacaoDto localizacao; // Opcional
}