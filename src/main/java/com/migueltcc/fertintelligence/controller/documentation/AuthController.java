package com.migueltcc.fertintelligence.controller.documentation;

import com.migueltcc.fertintelligence.dto.user.SignInRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Autenticação", description = "Serviço de Autenticação")
public interface AuthController {

    @Operation(description = "Autenticar", summary = "Buscar Token de Autenticação")
    String authenticate(SignInRequestDto request);
}
