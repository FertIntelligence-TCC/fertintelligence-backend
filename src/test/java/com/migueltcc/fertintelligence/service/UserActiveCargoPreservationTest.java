package com.migueltcc.fertintelligence.service;

import com.migueltcc.fertintelligence.composedAttributes.user.AccessRequestStatus;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyAccessRequestModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.JwtService;
import com.migueltcc.fertintelligence.service.implementation.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class UserActiveCargoPreservationTest {

    @Test
    void preservesOwnershipAndRequestsAcrossAllFunctionalCargoTransitions() {
        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        JwtService jwtService = mock(JwtService.class);
        UserServiceImpl service = new UserServiceImpl(userRepository, passwordEncoder, jwtService);

        UserModel user = UserModel.builder()
                .id(41L)
                .username("owner")
                .cargo(Cargo.PROPRIETARIO)
                .build();
        PropertyModel property = PropertyModel.builder().id(51L).owner(user).build();
        PropertyAccessRequestModel pending = PropertyAccessRequestModel.builder()
                .id(61L)
                .property(property)
                .requester(user)
                .status(AccessRequestStatus.PENDING)
                .build();
        PropertyAccessRequestModel approved = PropertyAccessRequestModel.builder()
                .id(62L)
                .property(property)
                .requester(user)
                .status(AccessRequestStatus.APPROVED)
                .build();
        PropertyAccessRequestModel rejected = PropertyAccessRequestModel.builder()
                .id(63L)
                .property(property)
                .requester(user)
                .status(AccessRequestStatus.REJECTED)
                .build();
        List<PropertyAccessRequestModel> requests = List.of(pending, approved, rejected);

        when(userRepository.findByUsername("owner")).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(jwtService.generateToken(eq("owner"), any(Cargo.class))).thenReturn("fresh-token");

        for (Cargo target : List.of(
                Cargo.AGRONOMO_RESIDENTE,
                Cargo.PROPRIETARIO,
                Cargo.GERENTE,
                Cargo.SECRETARIO,
                Cargo.AGRONOMO_CONSULTOR,
                Cargo.SUPERVISOR_DE_AREA)) {
            service.updateActiveCargo("owner", target);
            assertThat(user.getCargo()).isEqualTo(target);
            assertThat(user.getId()).isEqualTo(41L);
            assertThat(property.getOwner()).isSameAs(user);
            assertThat(property.getId()).isEqualTo(51L);
            assertThat(requests).extracting(PropertyAccessRequestModel::getId)
                    .containsExactly(61L, 62L, 63L);
            assertThat(requests).extracting(PropertyAccessRequestModel::getStatus)
                    .containsExactly(
                            AccessRequestStatus.PENDING,
                            AccessRequestStatus.APPROVED,
                            AccessRequestStatus.REJECTED);
            assertThat(requests).allMatch(request -> request.getRequester() == user);
        }

        verify(userRepository, times(6)).findByUsername("owner");
        verify(userRepository, times(6)).save(user);
        verifyNoMoreInteractions(userRepository);
    }
}
