package com.migueltcc.fertintelligence.entities.fertintelligence;
import com.migueltcc.fertintelligence.composedAtributes.Cargo;
import com.migueltcc.fertintelligence.composedAtributes.DataNasc;
import com.migueltcc.fertintelligence.composedAtributes.Formacao;
import com.migueltcc.fertintelligence.composedAtributes.Genero;
import com.migueltcc.fertintelligence.dto.user.UserResponseDto;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
@Table(name = "USUARIOS")
@EqualsAndHashCode
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NOME", nullable = false)
    private String nome;

    @Column(name = "CPF", unique = true, nullable = false, length = 11)
    private String cpf;

    @Column(name = "EMAIL", unique = true, nullable = false)
    private String email;

    @Embedded
    @Column(name = "DATANASC", nullable = false)
    private DataNasc datanasc;

    @Enumerated(EnumType.STRING)
    @Column(name = "GENERO", nullable = false)
    private Genero genero;

    @Column(name = "TELEFONE", nullable = false)
    private String telefone;

    @Enumerated(EnumType.STRING)
    @Column(name = "FORMACAO", nullable = false)
    private Formacao formacao;

    @Column(name = "PROFISSAO", nullable = false)
    private String profissao;

    @Column(name = "SENHA", nullable = false)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(name = "CARGA", nullable = false)
    private Cargo cargo;

    public UserResponseDto toDto() {
        return UserResponseDto.builder()
                .id(this.id)
                .email(this.email)
                .nome(this.nome)
                .build();
    }

}
