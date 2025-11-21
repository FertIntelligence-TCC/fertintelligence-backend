package com.migueltcc.fertintelligence.model.fertintelligence;

import com.migueltcc.fertintelligence.composedAttributes.user.AccessRequestStatus;
import com.migueltcc.fertintelligence.dto.plotAccessRequest.PlotAccessRequestResponseDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
@Table(name = "SOLITACOES_DE_ACESSO_A_TALHOES")
@EqualsAndHashCode
public class PlotAccessRequestModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_PROPRIEDADE", nullable = false)
    private PropertyModel property;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_SOLICITANTE", nullable = false)
    private UserModel requester;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private AccessRequestStatus status;

    @Column(name = "CRIADO_EM", nullable = false)
    private LocalDateTime createdAt;

    public PlotAccessRequestResponseDto toDto() {
        return PlotAccessRequestResponseDto.builder()
                .id(this.id)
                .propertyId(this.property.getId())
                .propertyName(this.property.getNome())
                .requesterId(this.requester.getId())
                .requesterName(this.requester.getName())
                .requesterCargo(this.requester.getCargo())
                .requesterEmail(this.requester.getEmail())
                .requesterCpf(this.requester.getCpf())
                .status(this.status)
                .createdAt(this.createdAt)
                .build();
    }
}