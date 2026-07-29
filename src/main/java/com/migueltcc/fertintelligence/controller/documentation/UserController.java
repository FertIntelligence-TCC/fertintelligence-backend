package com.migueltcc.fertintelligence.controller.documentation;

import com.migueltcc.fertintelligence.dto.user.UserCreateRequestDto;
import com.migueltcc.fertintelligence.dto.user.ActiveCargoUpdateRequestDto;
import com.migueltcc.fertintelligence.dto.user.ActiveCargoUpdateResponseDto;
import com.migueltcc.fertintelligence.dto.user.UserPostRequestDto;
import com.migueltcc.fertintelligence.dto.user.UserResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Usuário", description = "Serviço de Usuário")
public interface UserController {
    @Operation(description = "Registrar usuário", summary = "Registrar novo usuário no sistema")
    ResponseEntity<String> createUser(
            @RequestBody UserCreateRequestDto userDto);

    @Operation(description = "Atualizar usuário", summary = "Atualizar usuário no sistema. Deixe o campo vazio para mantê-lo.")
    ResponseEntity<String> updateUser(
            @RequestBody() UserPostRequestDto userDto,
            Authentication authentication);

    @Operation(description = "Alterar o contexto funcional atual do usuário", summary = "Alterar cargo ativo")
    ResponseEntity<ActiveCargoUpdateResponseDto> updateActiveCargo(
            @RequestBody ActiveCargoUpdateRequestDto request,
            Authentication authentication);

    @Operation(description = "Deletar usuário", summary = "Remover usuário do sistema.")
    ResponseEntity<String> deleteUser(Authentication authentication);

    @Operation(description = "Ler usuário", summary = "Pegar informações do usuário no sistema.")
    ResponseEntity<UserResponseDto> getUser(Authentication authentication);
}
