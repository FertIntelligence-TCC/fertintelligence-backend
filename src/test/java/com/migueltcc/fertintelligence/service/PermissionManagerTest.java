package com.migueltcc.fertintelligence.service;

import com.migueltcc.fertintelligence.composedAttributes.permissions.PermissionScope;
import com.migueltcc.fertintelligence.composedAttributes.permissions.PermissionType;
import com.migueltcc.fertintelligence.composedAttributes.user.AccessRequestStatus;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotAccessRequestModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyAccessRequestModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.repository.PlotAccessRequestRepository;
import com.migueltcc.fertintelligence.repository.PlotRepository;
import com.migueltcc.fertintelligence.repository.PropertyAccessRequestRepository;
import com.migueltcc.fertintelligence.repository.PropertyRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.implementation.PermissionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.access.AccessDeniedException;

import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PermissionManagerTest {

    @Mock private UserRepository userRepository;
    @Mock private PropertyRepository propertyRepository;
    @Mock private PlotRepository plotRepository;

    @Mock private PropertyAccessRequestRepository propertyAccessRequestRepository;
    @Mock private PlotAccessRequestRepository plotAccessRequestRepository;

    @InjectMocks
    private PermissionManager permissionManager;

    private UserModel user;
    private UserModel owner;
    private UserModel manager;
    private PropertyModel property;
    private PlotModel plot;

    @BeforeEach
    void setup() {
        user = mock(UserModel.class);
        owner = mock(UserModel.class);
        manager = mock(UserModel.class);

        when(user.getId()).thenReturn(100L);
        when(owner.getId()).thenReturn(1L);
        when(manager.getId()).thenReturn(2L);

        property = mock(PropertyModel.class);
        when(property.getId()).thenReturn(10L);
        when(property.getOwner()).thenReturn(owner);
        when(property.getManager()).thenReturn(manager);

        plot = mock(PlotModel.class);
        when(plot.getId()).thenReturn(50L);
        when(plot.getProperty()).thenReturn(property);

        // ✅ membership aprovado (sempre)
        when(propertyAccessRequestRepository.findByPropertyAndRequesterAndStatus(any(), any(), eq(AccessRequestStatus.APPROVED)))
                .thenReturn(Optional.of(mock(PropertyAccessRequestModel.class)));
    }

    @Test
    @DisplayName("Permissão global PROPERTY libera análises e culturas em qualquer talhão")
    void globalPermissionAllowsAllPlots() {
        when(plotAccessRequestRepository.existsByPropertyAndRequesterAndScopeAndPermissionTypeInAndStatus(
                eq(property),
                eq(user),
                eq(PermissionScope.PROPERTY),
                argThat(types -> types != null && types.contains(PermissionType.EDIT_ANALYSES_AND_CROPS)),
                eq(AccessRequestStatus.APPROVED)
        )).thenReturn(true);

        assertDoesNotThrow(() -> permissionManager.assertCanEditAnalyses(property, plot, user));
        assertDoesNotThrow(() -> permissionManager.assertCanEditCrops(property, plot, user));
    }

    @Test
    @DisplayName("Permissão por talhão PLOT EDIT_ANALYSES libera apenas análises")
    void plotScopedAnalysesOnly() {
        when(plotAccessRequestRepository.existsByPropertyAndRequesterAndScopeAndPermissionTypeInAndStatus(
                eq(property),
                eq(user),
                eq(PermissionScope.PROPERTY),
                any(),
                eq(AccessRequestStatus.APPROVED)
        )).thenReturn(false);

        when(plotAccessRequestRepository.existsByPropertyAndPlotAndRequesterAndScopeAndPermissionTypeInAndStatus(
                eq(property),
                eq(plot),
                eq(user),
                eq(PermissionScope.PLOT),
                argThat(types ->
                        types != null
                                && types.contains(PermissionType.EDIT_ANALYSES)
                                && !types.contains(PermissionType.EDIT_CROPS)
                ),
                eq(AccessRequestStatus.APPROVED)
        )).thenReturn(true);

        assertDoesNotThrow(() -> permissionManager.assertCanEditAnalyses(property, plot, user));
        assertThrows(AccessDeniedException.class,
                () -> permissionManager.assertCanEditCrops(property, plot, user));
    }

    @Test
    void generateRecommendationComAcessoPorTalhaoPermitido() {
        when(plotAccessRequestRepository.existsByPropertyAndRequesterAndScopeAndStatus(
                any(), any(), eq(PermissionScope.PROPERTY), eq(AccessRequestStatus.APPROVED)
        )).thenReturn(false);
        when(plotAccessRequestRepository.existsByPropertyAndPlotAndRequesterAndScopeAndStatus(
                any(), any(), any(), eq(PermissionScope.PLOT), eq(AccessRequestStatus.APPROVED)
        )).thenReturn(true);

        assertDoesNotThrow(() -> permissionManager.assertCanGenerateRecommendation(property, plot, user));
    }

    @Test
    void printRecommendationSomenteAgronomos() {
        UserModel residente = mock(UserModel.class);
        when(residente.getCargo()).thenReturn(Cargo.AGRONOMO_RESIDENTE);
        assertDoesNotThrow(() -> permissionManager.assertCanPrintRecommendation(residente));

        UserModel gerenteUser = mock(UserModel.class);
        when(gerenteUser.getCargo()).thenReturn(Cargo.GERENTE);
        assertThrows(AccessDeniedException.class, () -> permissionManager.assertCanPrintRecommendation(gerenteUser));
    }
}
