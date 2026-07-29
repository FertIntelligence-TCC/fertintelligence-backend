package com.migueltcc.fertintelligence.service.documentation;

import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.user.UserCreateRequestDto;
import com.migueltcc.fertintelligence.dto.user.ActiveCargoUpdateResponseDto;
import com.migueltcc.fertintelligence.dto.user.UserPostRequestDto;
import com.migueltcc.fertintelligence.dto.user.UserResponseDto;

import java.util.List;

public interface UserService {
    String createUser(UserCreateRequestDto userDTO);
    String updateUser(String userName, UserPostRequestDto request);
    ActiveCargoUpdateResponseDto updateActiveCargo(String userName, Cargo cargo);
    String deleteUser(String username);
    UserResponseDto getUser(String username);
}
