package com.migueltcc.fertintelligence.model.fertintelligence;

import com.migueltcc.fertintelligence.composedAttributes.permissions.PermissionScope;
import com.migueltcc.fertintelligence.composedAttributes.permissions.PermissionType;
import com.migueltcc.fertintelligence.composedAttributes.permissions.PermissionType;
import com.migueltcc.fertintelligence.composedAttributes.permissions.PermissionScope;
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

    // null => scope PROPERTY (todos os talhões)
    @ManyToOne
    @JoinColumn(name = "ID_TALHAO")
    private PlotModel plot;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_SOLICITANTE", nullable = false)
    private UserModel requester;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESCOPO", nullable = false)
    private PermissionScope scope;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_PERMISSAO", nullable = false)
    private PermissionType permissionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private AccessRequestStatus status;

    @Column(name = "CRIADO_EM", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        // se plot é null, por padrão o escopo é PROPERTY
        if (scope == null) scope = (plot == null) ? PermissionScope.PROPERTY : PermissionScope.PLOT;
    }

    public PlotAccessRequestResponseDto toDto() {
        return PlotAccessRequestResponseDto.builder()
                .id(this.id)
                .propertyId(this.property.getId())
                .propertyName(this.property.getNome())
                .plotId(this.plot != null ? this.plot.getId() : null)
                .plotIdentification(this.plot != null ? this.plot.getIdentification() : null)
                .requesterId(this.requester.getId())
                .requesterName(this.requester.getName())
                .requesterCargo(this.requester.getCargo())
                .requesterEmail(this.requester.getEmail())
                .requesterCpf(this.requester.getCpf())
                .scope(this.scope)
                .permissionType(this.permissionType)
                .status(this.status)
                .createdAt(this.createdAt)
                .build();
    }
}