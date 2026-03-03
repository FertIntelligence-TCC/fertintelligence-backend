package com.migueltcc.fertintelligence.service;

import com.migueltcc.fertintelligence.composedAttributes.permissions.PermissionScope;
import com.migueltcc.fertintelligence.composedAttributes.permissions.PermissionType;
import com.migueltcc.fertintelligence.composedAttributes.user.AccessRequestStatus;
import com.migueltcc.fertintelligence.dto.permissions.EffectivePermissionsResponseDto;
import com.migueltcc.fertintelligence.dto.permissions.PlotSummaryDto;
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
import com.migueltcc.fertintelligence.service.implementation.EffectivePermissionsServiceImpl;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) // ✅ resolve UnnecessaryStubbingException
class EffectivePermissionsServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private PropertyRepository propertyRepository;
    @Mock private PlotRepository plotRepository;
    @Mock private PropertyAccessRequestRepository propertyAccessRequestRepository;
    @Mock private PlotAccessRequestRepository plotAccessRequestRepository;

    @InjectMocks
    private EffectivePermissionsServiceImpl service;

    private UserModel user;
    private UserModel owner;
    private UserModel manager;
    private PropertyModel property;

    private PlotModel p1;
    private PlotModel p2;
    private PlotModel p3;

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

        p1 = mockPlot(1L, "P1", property);
        p2 = mockPlot(2L, "P2", property);
        p3 = mockPlot(3L, "P3", property);

        when(plotRepository.findAllByProperty(property)).thenReturn(List.of(p1, p2, p3));
    }

    private PlotModel mockPlot(Long id, String identification, PropertyModel property) {
        PlotModel p = mock(PlotModel.class);
        when(p.getId()).thenReturn(id);
        when(p.getIdentification()).thenReturn(identification);
        when(p.getProperty()).thenReturn(property);
        return p;
    }

    @Test
    @DisplayName("Owner: flags true e counts = total de talhões; lista de editáveis = todos")
    void ownerGetsAll() {
        when(userRepository.findByUsername("ownerUser")).thenReturn(Optional.of(owner));
        when(propertyRepository.findById(10L)).thenReturn(Optional.of(property));

        EffectivePermissionsResponseDto dto = service.getEffectivePermissions(10L, "ownerUser");

        assertTrue(dto.isCanManageProperty());
        assertTrue(dto.isCanEditAllPlotsAnalyses());
        assertTrue(dto.isCanEditAllPlotsCrops());
        assertEquals(3, dto.getPlotsEditableAnalysesCount());
        assertEquals(3, dto.getPlotsEditableCropsCount());

        List<PlotSummaryDto> analyses = service.getEditableAnalysesPlots(10L, "ownerUser");
        List<PlotSummaryDto> crops = service.getEditableCropsPlots(10L, "ownerUser");

        assertEquals(3, analyses.size());
        assertEquals(3, crops.size());
    }

    @Test
    @DisplayName("Sem membership aprovado: deve negar")
    void withoutMembershipDenied() {
        when(userRepository.findByUsername("u")).thenReturn(Optional.of(user));
        when(propertyRepository.findById(10L)).thenReturn(Optional.of(property));
        when(propertyAccessRequestRepository.findByPropertyAndRequesterAndStatus(property, user, AccessRequestStatus.APPROVED))
                .thenReturn(Optional.empty());

        assertThrows(AccessDeniedException.class, () -> service.getEffectivePermissions(10L, "u"));
        assertThrows(AccessDeniedException.class, () -> service.getEditableAnalysesPlots(10L, "u"));
        assertThrows(AccessDeniedException.class, () -> service.getEditableCropsPlots(10L, "u"));
    }

    @Test
    @DisplayName("Com membership + permissão global (PROPERTY + EDIT_ANALYSES_AND_CROPS): counts=total e lista=todo mundo")
    void membershipWithGlobalAllowsAll() {
        when(userRepository.findByUsername("u")).thenReturn(Optional.of(user));
        when(propertyRepository.findById(10L)).thenReturn(Optional.of(property));
        when(propertyAccessRequestRepository.findByPropertyAndRequesterAndStatus(property, user, AccessRequestStatus.APPROVED))
                .thenReturn(Optional.of(mock(PropertyAccessRequestModel.class))); // ✅ tipo correto

        PlotAccessRequestModel global = mock(PlotAccessRequestModel.class);
        when(global.getStatus()).thenReturn(AccessRequestStatus.APPROVED);
        when(global.getScope()).thenReturn(PermissionScope.PROPERTY);
        when(global.getPlot()).thenReturn(null);
        when(global.getPermissionType()).thenReturn(PermissionType.EDIT_ANALYSES_AND_CROPS);

        when(plotAccessRequestRepository.findAllByRequesterAndProperty(user, property))
                .thenReturn(List.of(global));

        EffectivePermissionsResponseDto dto = service.getEffectivePermissions(10L, "u");

        assertFalse(dto.isCanManageProperty());
        assertTrue(dto.isCanEditAllPlotsAnalyses());
        assertTrue(dto.isCanEditAllPlotsCrops());
        assertEquals(3, dto.getPlotsEditableAnalysesCount());
        assertEquals(3, dto.getPlotsEditableCropsCount());

        assertEquals(3, service.getEditableAnalysesPlots(10L, "u").size());
        assertEquals(3, service.getEditableCropsPlots(10L, "u").size());
    }

    @Test
    @DisplayName("Com membership + permissão por talhão: análises só em P1/P3; culturas só em P3")
    void membershipWithPlotScopedPermissions() {
        when(userRepository.findByUsername("u")).thenReturn(Optional.of(user));
        when(propertyRepository.findById(10L)).thenReturn(Optional.of(property));
        when(propertyAccessRequestRepository.findByPropertyAndRequesterAndStatus(property, user, AccessRequestStatus.APPROVED))
                .thenReturn(Optional.of(mock(PropertyAccessRequestModel.class))); // ✅ tipo correto

        PlotAccessRequestModel a1 = mockRequest(PermissionScope.PLOT, p1, PermissionType.EDIT_ANALYSES);
        PlotAccessRequestModel a3 = mockRequest(PermissionScope.PLOT, p3, PermissionType.EDIT_ANALYSES);
        PlotAccessRequestModel c3 = mockRequest(PermissionScope.PLOT, p3, PermissionType.EDIT_CROPS);

        when(plotAccessRequestRepository.findAllByRequesterAndProperty(user, property))
                .thenReturn(List.of(a1, a3, c3));

        EffectivePermissionsResponseDto dto = service.getEffectivePermissions(10L, "u");

        assertFalse(dto.isCanEditAllPlotsAnalyses());
        assertFalse(dto.isCanEditAllPlotsCrops());
        assertEquals(2, dto.getPlotsEditableAnalysesCount());
        assertEquals(1, dto.getPlotsEditableCropsCount());

        List<PlotSummaryDto> analyses = service.getEditableAnalysesPlots(10L, "u");
        List<PlotSummaryDto> crops = service.getEditableCropsPlots(10L, "u");

        assertEquals(List.of(1L, 3L), analyses.stream().map(PlotSummaryDto::getId).sorted().toList());
        assertEquals(List.of(3L), crops.stream().map(PlotSummaryDto::getId).toList());
    }

    private PlotAccessRequestModel mockRequest(PermissionScope scope, PlotModel plot, PermissionType type) {
        PlotAccessRequestModel r = mock(PlotAccessRequestModel.class);
        when(r.getStatus()).thenReturn(AccessRequestStatus.APPROVED);
        when(r.getScope()).thenReturn(scope);
        when(r.getPlot()).thenReturn(plot);
        when(r.getPermissionType()).thenReturn(type);
        return r;
    }
}