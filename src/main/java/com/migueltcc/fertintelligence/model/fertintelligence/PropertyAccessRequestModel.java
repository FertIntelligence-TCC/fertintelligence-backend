package com.migueltcc.fertintelligence.model.fertintelligence;

import com.migueltcc.fertintelligence.composedAttributes.user.AccessRequestStatus;
import com.migueltcc.fertintelligence.dto.propertyAccessRequest.PropertyAccessRequestResponseDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
@Table(name = "SOLITACOES_DE_ACESSO_A_PROPRIEDADES")
@EqualsAndHashCode
public class PropertyAccessRequestModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "PROPERTY_ID", nullable = false)
    private PropertyModel property;

    // @ManyToOne
    // @JoinColumn(name = "ID_TALHAO")
    // private PlotModel plot;

    @ManyToOne(optional = false)
    @JoinColumn(name = "REQUESTER_ID", nullable = false)
    private UserModel requester;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private AccessRequestStatus status;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    public PropertyAccessRequestResponseDto toDto() {
        return PropertyAccessRequestResponseDto.builder()
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