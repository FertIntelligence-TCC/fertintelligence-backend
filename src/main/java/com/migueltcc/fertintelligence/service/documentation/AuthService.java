package com.migueltcc.fertintelligence.service.documentation;

import org.springframework.security.core.Authentication;

public interface AuthService {
    String authenticate(Authentication authentication);
}

