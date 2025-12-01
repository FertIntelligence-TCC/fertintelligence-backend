package com.migueltcc.fertintelligence.model.fertintelligence;

import com.migueltcc.fertintelligence.composedAttributes.property.Localizacao;
import com.migueltcc.fertintelligence.dto.property.LocalizacaoDto;
import com.migueltcc.fertintelligence.dto.property.PropertyResponseDto;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
@Table(name = "PROPRIEDADES")
@EqualsAndHashCode
public class PropertyModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ID_CRIADOR", nullable = false)
    private UserModel owner;

    @Column(name = "NOME", unique = true, nullable = false)
    private String nome;

    @Column(name = "ENDERECO", nullable = false, length = 512)
    private String endereco;

    @Column(name = "CNPJ", unique = true, nullable = false, length = 18)
    private String cnpj;

    @Embedded
    @Column(name = "LOCALIZACAO", nullable = false)
    private Localizacao localizacao;

    public PropertyResponseDto toDto() {
        return PropertyResponseDto.builder()
                .id(this.id)
                .nome(this.nome)
                .endereco(this.endereco)
                .cnpj(this.cnpj)
                .localizacao(new LocalizacaoDto(
                        this.localizacao.getLatitude(),
                        this.localizacao.getLatDirection(),
                        this.localizacao.getLongitude(),
                        this.localizacao.getLongDirection(),
                        this.localizacao.getAltitude()
                ))
                .ownerId(this.owner.getId())
                .ownerNome(this.owner.getName())
                .build();
    }
}