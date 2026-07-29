package com.migueltcc.fertintelligence.config;

import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CurrentCargoJwtAuthenticationConverterTest {

    @Test
    void ignoresStaleRoleClaimAndUsesCurrentDatabaseCargo() {
        UserRepository repository = mock(UserRepository.class);
        when(repository.findByUsername("user")).thenReturn(Optional.of(
                UserModel.builder().username("user").cargo(Cargo.GERENTE).build()));
        Jwt jwt = Jwt.withTokenValue("old-token")
                .header("alg", "none")
                .subject("user")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(60))
                .claim("scope", "ROLE_PROPRIETARIO")
                .build();

        var authentication = new CurrentCargoJwtAuthenticationConverter(Optional.of(repository)).convert(jwt);

        assertThat(authentication.getName()).isEqualTo("user");
        assertThat(authentication.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_GERENTE");
    }
}
