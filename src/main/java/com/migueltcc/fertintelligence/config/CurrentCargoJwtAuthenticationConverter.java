package com.migueltcc.fertintelligence.config;

import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.repository.UserRepository;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CurrentCargoJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final Optional<UserRepository> userRepository;

    public CurrentCargoJwtAuthenticationConverter(Optional<UserRepository> userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        UserModel user = userRepository
                .orElseThrow(() -> new BadCredentialsException("Repositório de usuários indisponível."))
                .findByUsername(jwt.getSubject())
                .orElseThrow(() -> new BadCredentialsException("Usuário do token não foi encontrado."));
        var authorities = user.getCargo() == null
                ? List.<SimpleGrantedAuthority>of()
                : List.of(new SimpleGrantedAuthority("ROLE_" + user.getCargo().name()));
        return new JwtAuthenticationToken(jwt, authorities, jwt.getSubject());
    }
}
