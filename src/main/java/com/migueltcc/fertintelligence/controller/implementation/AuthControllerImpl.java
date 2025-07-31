package com.migueltcc.fertintelligence.controller.implementation;

import com.migueltcc.fertintelligence.controller.documentation.AuthController;
import com.migueltcc.fertintelligence.service.documentation.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/authentication")
@Validated
@CrossOrigin
@Slf4j
public class AuthControllerImpl implements AuthController {
    private final AuthService authService;

    @Autowired
    public AuthControllerImpl(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/authenticate")
    public String authenticate(Authentication authentication) {
        return authService.authenticate(authentication);
    }

}