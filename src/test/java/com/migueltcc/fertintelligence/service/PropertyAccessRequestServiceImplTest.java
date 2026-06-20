package com.migueltcc.fertintelligence.service;

import com.migueltcc.fertintelligence.composedAttributes.property.Localizacao;
import com.migueltcc.fertintelligence.composedAttributes.user.AccessRequestStatus;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.property.PropertyResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotAccessRequestModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyAccessRequestModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.repository.PlotAccessRequestRepository;
import com.migueltcc.fertintelligence.repository.PropertyAccessRequestRepository;
import com.migueltcc.fertintelligence.repository.PropertyRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.implementation.PropertyAccessRequestServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PropertyAccessRequestServiceImplTest {

    @Mock private PropertyAccessRequestRepository propertyAccessRequestRepository;
    @Mock private PropertyRepository propertyRepository;
    @Mock private PlotAccessRequestRepository plotAccessRequestRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private PropertyAccessRequestServiceImpl service;

    @Test
    void shouldReturnPropertyApprovedAtPropertyLevelForOperationalUser() {
        UserModel requester = user("residente", Cargo.AGRONOMO_RESIDENTE);
        PropertyModel property = property(10L, "Fazenda Residente");

        when(userRepository.findByUsername("residente")).thenReturn(Optional.of(requester));
        when(propertyAccessRequestRepository.findAllByRequesterAndStatus(requester, AccessRequestStatus.APPROVED))
                .thenReturn(List.of(PropertyAccessRequestModel.builder().property(property).requester(requester).build()));
        when(plotAccessRequestRepository.findAllByRequesterAndStatus(requester, AccessRequestStatus.APPROVED))
                .thenReturn(List.of());

        List<PropertyResponseDto> result = service.getApprovedPropertiesForUser("residente");

        assertThat(result).extracting(PropertyResponseDto::getId).containsExactly(10L);
    }

    @Test
    void shouldDeriveApprovedPropertiesFromPlotAccessWithoutDuplicatingProperties() {
        UserModel requester = user("consultor", Cargo.AGRONOMO_CONSULTOR);
        PropertyModel property = property(20L, "Fazenda Consultor");

        when(userRepository.findByUsername("consultor")).thenReturn(Optional.of(requester));
        when(propertyAccessRequestRepository.findAllByRequesterAndStatus(requester, AccessRequestStatus.APPROVED))
                .thenReturn(List.of(PropertyAccessRequestModel.builder().property(property).requester(requester).build()));
        when(plotAccessRequestRepository.findAllByRequesterAndStatus(requester, AccessRequestStatus.APPROVED))
                .thenReturn(List.of(
                        PlotAccessRequestModel.builder().property(property).requester(requester).build(),
                        PlotAccessRequestModel.builder().property(property).requester(requester).build()
                ));

        List<PropertyResponseDto> result = service.getApprovedPropertiesForUser("consultor");

        assertThat(result).extracting(PropertyResponseDto::getId).containsExactly(20L);
    }

    @Test
    void shouldReturnEmptyListWhenUserHasNoApprovedAccess() {
        UserModel requester = user("secretario", Cargo.SECRETARIO);

        when(userRepository.findByUsername("secretario")).thenReturn(Optional.of(requester));
        when(propertyAccessRequestRepository.findAllByRequesterAndStatus(requester, AccessRequestStatus.APPROVED))
                .thenReturn(List.of());
        when(plotAccessRequestRepository.findAllByRequesterAndStatus(requester, AccessRequestStatus.APPROVED))
                .thenReturn(List.of());

        List<PropertyResponseDto> result = service.getApprovedPropertiesForUser("secretario");

        assertThat(result).isEmpty();
    }

    private UserModel user(String username, Cargo cargo) {
        return UserModel.builder()
                .id((long) username.hashCode())
                .username(username)
                .name(username)
                .cargo(cargo)
                .build();
    }

    private PropertyModel property(Long id, String name) {
        UserModel owner = user("owner" + id, Cargo.PROPRIETARIO);
        return PropertyModel.builder()
                .id(id)
                .nome(name)
                .endereco("Endereço")
                .cnpj("00.000.000/000" + id)
                .localizacao(Localizacao.builder().build())
                .owner(owner)
                .build();
    }
}
