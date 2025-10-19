package com.migueltcc.fertintelligence.model.fertintelligence;
import com.migueltcc.fertintelligence.composedAtributes.Cargo;
import com.migueltcc.fertintelligence.composedAtributes.DataNasc;
import com.migueltcc.fertintelligence.composedAtributes.Formacao;
import com.migueltcc.fertintelligence.composedAtributes.Genero;
import com.migueltcc.fertintelligence.composedAtributes.Telefone;
import com.migueltcc.fertintelligence.dto.user.DataNascDto;
import com.migueltcc.fertintelligence.dto.user.TelefoneDto;
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
public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "USERNAME", unique = true, nullable = false)
    private String username;

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
    private Telefone telefone;

    @Enumerated(EnumType.STRING)
    @Column(name = "FORMACAO", nullable = false)
    private Formacao formacao;

    @Column(name = "PROFISSAO", nullable = false)
    private String profissao;

    @Enumerated(EnumType.STRING)
    @Column(name = "CARGO", nullable = false)
    private Cargo cargo;

    @Column(name = "SENHA", nullable = false)
    private String password;

    @Column(name = "NOME", nullable = false)
    private String name;

    public UserResponseDto toDto() {
        return UserResponseDto.builder()
                .id(this.id)
                .nome(this.name)
                .cpf(this.cpf)
                .email(this.email)
                .datanasc(new DataNascDto(
                        this.datanasc.getDia(),
                        this.datanasc.getMes(),
                        this.datanasc.getAno()
                ))
                .genero(this.genero.name())
                .formacao(this.formacao.name())
                .profissao(this.profissao)
                .cargo(this.cargo)
                .telefone(new TelefoneDto(
                        this.telefone.getPais(),
                        this.telefone.getDdd(),
                        this.telefone.getNumero()
                ))
                .build();
    }

}
