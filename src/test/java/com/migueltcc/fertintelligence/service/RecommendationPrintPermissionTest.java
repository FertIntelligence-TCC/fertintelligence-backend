package com.migueltcc.fertintelligence.service;

import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.service.implementation.PermissionManager;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class RecommendationPrintPermissionTest {

    private final PermissionManager permissionManager = new PermissionManager(
            mock(com.migueltcc.fertintelligence.repository.UserRepository.class),
            mock(com.migueltcc.fertintelligence.repository.PropertyRepository.class),
            mock(com.migueltcc.fertintelligence.repository.PlotRepository.class),
            mock(com.migueltcc.fertintelligence.repository.PropertyAccessRequestRepository.class),
            mock(com.migueltcc.fertintelligence.repository.PlotAccessRequestRepository.class)
    );

    @Test
    void permitsOnlyResidentConsultantAndSupreme() {
        for (Cargo cargo : new Cargo[]{
                Cargo.AGRONOMO_RESIDENTE,
                Cargo.AGRONOMO_CONSULTOR,
                Cargo.USUARIO_SUPREMO
        }) {
            assertThatCode(() -> permissionManager.assertCanPrintRecommendation(user(cargo)))
                    .doesNotThrowAnyException();
            org.assertj.core.api.Assertions.assertThat(
                    com.migueltcc.fertintelligence.dto.recommendation.RecommendationResponseDto
                            .isPrintableForRole(cargo))
                    .isTrue();
        }

        for (Cargo cargo : new Cargo[]{
                Cargo.PROPRIETARIO,
                Cargo.GERENTE,
                Cargo.SECRETARIO,
                Cargo.SUPERVISOR_DE_AREA
        }) {
            assertThatThrownBy(() -> permissionManager.assertCanPrintRecommendation(user(cargo)))
                    .isInstanceOf(AccessDeniedException.class);
            org.assertj.core.api.Assertions.assertThat(
                    com.migueltcc.fertintelligence.dto.recommendation.RecommendationResponseDto
                            .isPrintableForRole(cargo))
                    .isFalse();
        }
    }

    @Test
    void rejectsMissingAuthenticationContext() {
        assertThatThrownBy(() -> permissionManager.assertCanPrintRecommendation(null))
                .isInstanceOf(AccessDeniedException.class);
    }

    private UserModel user(Cargo cargo) {
        return UserModel.builder().cargo(cargo).build();
    }
}
