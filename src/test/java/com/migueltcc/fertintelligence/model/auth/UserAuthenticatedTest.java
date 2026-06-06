package com.migueltcc.fertintelligence.model.auth;

import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserAuthenticatedTest {

    @Test
    void exposesCargoAsRoleAuthority() {
        UserModel user = UserModel.builder()
                .username("supremo")
                .password("password")
                .cargo(Cargo.USUARIO_SUPREMO)
                .build();

        UserAuthenticated authenticated = new UserAuthenticated(user);

        assertThat(authenticated.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_USUARIO_SUPREMO");
    }
}
