package com.migueltcc.fertintelligence.dto.plotAccessRequest;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.migueltcc.fertintelligence.composedAttributes.permissions.PermissionScope;
import com.migueltcc.fertintelligence.composedAttributes.permissions.PermissionType;
import com.migueltcc.fertintelligence.composedAttributes.user.AccessRequestStatus;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlotAccessRequestResponseDto {

    @Schema(description = "ID único da solicitação", example = "12")
    @JsonProperty("id")
    private Long id;

    @Schema(description = "ID da propriedade solicitada", example = "25")
    @JsonProperty("id_propriedade")
    private Long propertyId;

    @Schema(description = "Nome da propriedade solicitada", example = "Fazenda Santa Cecília")
    @JsonProperty("nome_propriedade")
    private String propertyName;

    @Schema(description = "ID do talhão solicitado, quando aplicável", example = "8")
    @JsonProperty("id_talhao")
    private Long plotId;

    @Schema(description = "Identificação do talhão solicitado, quando aplicável", example = "Talhão A")
    @JsonProperty("identificacao_talhao")
    private String plotIdentification;

    @Schema(description = "ID do usuário solicitante", example = "10")
    @JsonProperty("id_solicitante")
    private Long requesterId;

    @Schema(description = "Nome completo do solicitante", example = "Miguel Silva")
    @JsonProperty("nome_solicitante")
    private String requesterName;

    @Schema(description = "Cargo do solicitante", example = "AGRONOMO_RESIDENTE")
    @JsonProperty("cargo_solicitante")
    private Cargo requesterCargo;

    @Schema(description = "E-mail do solicitante", example = "miguel.silva@email.com")
    @JsonProperty("email_solicitante")
    private String requesterEmail;

    @Schema(description = "CPF do solicitante", example = "123.456.789-00")
    @JsonProperty("cpf_solicitante")
    private String requesterCpf;

    @Schema(description = "Status atual da solicitação", example = "PENDING")
    @JsonProperty("status")
    private AccessRequestStatus status;

    @Schema(description = "Data e hora em que a solicitação foi criada", example = "2025-11-20T14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("Escopo")
    private PermissionScope scope;

    @JsonProperty("tipo_permissao")
    private PermissionType permissionType;
}