package com.migueltcc.fertintelligence.service.documentation;

import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import org.springframework.security.core.Authentication;

public interface JwtService {
    String generateToken(Authentication authentication);
    String generateToken(String username, Cargo cargo);
}
