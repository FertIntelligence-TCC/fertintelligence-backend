// Local: /com/migueltcc/fertintelligence/dto/property/PropertyResponseDto.java
package com.migueltcc.fertintelligence.dto.property;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PropertyResponseDto {
    private Long id;
    private String nome;
    private String endereco;
    private String cnpj;
    private LocalizacaoDto localizacao;
    private Long ownerId;
    private String ownerNome;
}