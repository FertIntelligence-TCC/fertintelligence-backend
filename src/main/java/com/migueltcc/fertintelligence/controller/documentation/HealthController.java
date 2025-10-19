package com.migueltcc.fertintelligence.controller.documentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Health Check", description = "Serviço de verificação do status da API")
public interface HealthController {

    @Operation(description = "Checar saúde", summary = "Verifica se a API está online e funcionando corretamente")
    public ResponseEntity<String> healthCheck();
}