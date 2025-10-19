package com.migueltcc.fertintelligence.service.documentation;

import com.migueltcc.fertintelligence.dto.user.SignInRequestDto;

public interface AuthService {
    String authenticate(SignInRequestDto request);
}
