package com.migueltcc.fertintelligence.service.documentation;

import com.migueltcc.fertintelligence.dto.user.UserCreateRequestDto;
import com.migueltcc.fertintelligence.dto.user.UserPostRequestDto;
import com.migueltcc.fertintelligence.dto.user.UserResponseDto;

public interface UserService {
    String createUser(UserCreateRequestDto userDTO);
    String postUser(String userName, UserPostRequestDto request);
    String deleteUser(String username);
    UserResponseDto getUser(String username);

}