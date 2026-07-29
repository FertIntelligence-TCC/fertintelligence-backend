package com.migueltcc.fertintelligence.service.auth;

import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.service.documentation.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.stream.Collectors;

@Service
public class JwtServiceImpl implements JwtService {
    private final JwtEncoder encoder;

    @Autowired
    public JwtServiceImpl(JwtEncoder encoder) {
        this.encoder = encoder;
    }

    public String generateToken(Authentication authentication) {
        String scopes = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        return generateToken(authentication.getName(), scopes);
    }

    @Override
    public String generateToken(String username, Cargo cargo) {
        return generateToken(username, "ROLE_" + cargo.name());
    }

    private String generateToken(String username, String scopes) {
        Instant now = Instant.now();
        long expiry = 24 * 3600L;

        var claims = JwtClaimsSet.builder()
                .issuer("spring-security-jwt")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiry))
                .subject(username)
                .claim("scope", scopes)
                .claim("scopes", scopes)
                .build();

        return encoder.encode(JwtEncoderParameters.from(claims))
                .getTokenValue();
    }
}
