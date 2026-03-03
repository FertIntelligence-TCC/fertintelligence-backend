package com.migueltcc.fertintelligence.dto.plotAccessRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.migueltcc.fertintelligence.composedAttributes.permissions.PermissionType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlotAccessRequestCreateRequestDto {

    @JsonProperty("id_propriedade")
    @NotNull
    private Long propertyId;

    @JsonProperty("id_talhao")
    private Long plotId; // null => todos os talhões (residente)

    @JsonProperty("tipo_permissao")
    private PermissionType permissionType; // ✅ agora pode ser null (inferido pelo cargo)
}