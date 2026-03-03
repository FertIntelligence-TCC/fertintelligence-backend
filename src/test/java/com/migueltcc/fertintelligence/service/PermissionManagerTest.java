package com.migueltcc.fertintelligence.service;

import com.migueltcc.fertintelligence.composedAttributes.permissions.PermissionScope;
import com.migueltcc.fertintelligence.composedAttributes.permissions.PermissionType;
import com.migueltcc.fertintelligence.composedAttributes.user.AccessRequestStatus;
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
    @DisplayName("Permissão global (PROPERTY + EDIT_ANALYSES_AND_CROPS) libera análises e culturas em qualquer plot")
    void globalPermissionAllowsAllPlots() {
        // ===== Se PermissionManager usar EXISTS (otimizado) =====
        when(plotAccessRequestRepository.existsByPropertyAndRequesterAndScopeAndPermissionTypeInAndStatus(
                any(), any(), any(), any(Collection.class), any()
        )).thenAnswer(inv -> {
            PermissionScope scope = inv.getArgument(2);
            Collection<PermissionType> types = inv.getArgument(3);
            AccessRequestStatus status = inv.getArgument(4);

            return scope == PermissionScope.PROPERTY
                    && status == AccessRequestStatus.APPROVED
                    && types != null
                    && types.contains(PermissionType.EDIT_ANALYSES_AND_CROPS);
        });

        when(plotAccessRequestRepository.existsByPropertyAndPlotAndRequesterAndScopeAndPermissionTypeInAndStatus(
                any(), any(), any(), any(), any(Collection.class), any()
        )).thenReturn(false);

        // ===== Se PermissionManager usar FIND (compat) =====
        when(plotAccessRequestRepository.findByPropertyAndRequesterAndScopeAndPermissionTypeAndStatus(
                any(), any(), any(), any(), any()
        )).thenAnswer(inv -> {
            PermissionScope scope = inv.getArgument(2);
            PermissionType type = inv.getArgument(3);
            AccessRequestStatus status = inv.getArgument(4);

            if (scope == PermissionScope.PROPERTY
                    && status == AccessRequestStatus.APPROVED
                    && type == PermissionType.EDIT_ANALYSES_AND_CROPS) {
                return Optional.of(mock(PlotAccessRequestModel.class));
            }
            return Optional.empty();
        });

        when(plotAccessRequestRepository.findByPropertyAndPlotAndRequesterAndScopeAndPermissionTypeAndStatus(
                any(), any(), any(), any(), any(), any()
        )).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> permissionManager.assertCanEditAnalyses(property, plot, user));
        assertDoesNotThrow(() -> permissionManager.assertCanEditCrops(property, plot, user));
    }

    @Test
    @DisplayName("Permissão por talhão (PLOT + EDIT_ANALYSES) libera apenas análises; culturas devem negar")
    void plotScopedAnalysesOnly() {
        // ===== Global vazio =====
        when(plotAccessRequestRepository.existsByPropertyAndRequesterAndScopeAndPermissionTypeInAndStatus(
                any(), any(), any(), any(Collection.class), any()
        )).thenReturn(false);

        when(plotAccessRequestRepository.findByPropertyAndRequesterAndScopeAndPermissionTypeAndStatus(
                any(), any(), any(), any(), any()
        )).thenReturn(Optional.empty());

        // ===== Plot: permite somente EDIT_ANALYSES =====
        when(plotAccessRequestRepository.existsByPropertyAndPlotAndRequesterAndScopeAndPermissionTypeInAndStatus(
                any(), any(), any(), any(), any(Collection.class), any()
        )).thenAnswer(inv -> {
            PermissionScope scope = inv.getArgument(3);
            Collection<PermissionType> types = inv.getArgument(4);
            AccessRequestStatus status = inv.getArgument(5);

            return scope == PermissionScope.PLOT
                    && status == AccessRequestStatus.APPROVED
                    && types != null
                    && types.contains(PermissionType.EDIT_ANALYSES);
        });

        when(plotAccessRequestRepository.findByPropertyAndPlotAndRequesterAndScopeAndPermissionTypeAndStatus(
                any(), any(), any(), any(), any(), any()
        )).thenAnswer(inv -> {
            PermissionScope scope = inv.getArgument(3);
            PermissionType type = inv.getArgument(4);
            AccessRequestStatus status = inv.getArgument(5);

            if (scope == PermissionScope.PLOT
                    && status == AccessRequestStatus.APPROVED
                    && type == PermissionType.EDIT_ANALYSES) {
                return Optional.of(mock(PlotAccessRequestModel.class));
            }
            return Optional.empty();
        });

        assertDoesNotThrow(() -> permissionManager.assertCanEditAnalyses(property, plot, user));
        assertThrows(AccessDeniedException.class, () -> permissionManager.assertCanEditCrops(property, plot, user));
    }
}