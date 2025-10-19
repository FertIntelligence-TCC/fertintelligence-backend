package com.migueltcc.fertintelligence.service.auth;

import com.migueltcc.fertintelligence.dto.user.SignInRequestDto;
import com.migueltcc.fertintelligence.service.documentation.AuthService;
import com.migueltcc.fertintelligence.service.documentation.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthServiceImpl(JwtService jwtService, AuthenticationManager authenticationManager) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public String authenticate(SignInRequestDto request) {
        // Autentica o usuário com nome + senha
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        // Gera o token JWT
        return jwtService.generateToken(authentication);
    }
}
