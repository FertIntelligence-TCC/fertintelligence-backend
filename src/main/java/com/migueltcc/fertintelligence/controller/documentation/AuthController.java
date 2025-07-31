package com.migueltcc.fertintelligence.controller.documentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;

@Tag(name = "Autenticação", description = "Serviço de Autenticação")
public interface AuthController {

    @Operation(description = "Autenticar", summary = "Buscar Token de Autenticação")
    public String authenticate(Authentication authentication);
}
