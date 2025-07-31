package com.migueltcc.fertintelligence.service.documentation;

import org.springframework.security.core.Authentication;

public interface JwtService {
    String generateToken(Authentication authentication);
}
