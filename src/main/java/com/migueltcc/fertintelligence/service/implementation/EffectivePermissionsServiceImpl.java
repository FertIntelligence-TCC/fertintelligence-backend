package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.permissions.PermissionScope;
import com.migueltcc.fertintelligence.composedAttributes.permissions.PermissionType;
import com.migueltcc.fertintelligence.composedAttributes.user.AccessRequestStatus;
import com.migueltcc.fertintelligence.dto.permissions.EffectivePermissionsResponseDto;
import com.migueltcc.fertintelligence.dto.permissions.PlotSummaryDto;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotAccessRequestModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.repository.PlotAccessRequestRepository;
import com.migueltcc.fertintelligence.repository.PlotRepository;
import com.migueltcc.fertintelligence.repository.PropertyAccessRequestRepository;
import com.migueltcc.fertintelligence.repository.PropertyRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.EffectivePermissionsService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EffectivePermissionsServiceImpl implements EffectivePermissionsService {

    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final PlotRepository plotRepository;

    private final PropertyAccessRequestRepository propertyAccessRequestRepository;
    private final PlotAccessRequestRepository plotAccessRequestRepository;

    // Mesmas regras do PermissionManager (ANALYSES_ALLOWED / CROPS_ALLOWED)
    private static final EnumSet<PermissionType> ANALYSES_ALLOWED =
            EnumSet.of(PermissionType.EDIT_ANALYSES, PermissionType.EDIT_ANALYSES_AND_CROPS);

    private static final EnumSet<PermissionType> CROPS_ALLOWED =
            EnumSet.of(PermissionType.EDIT_CROPS, PermissionType.EDIT_ANALYSES_AND_CROPS);

    @Override
    @Transactional(readOnly = true)
    public EffectivePermissionsResponseDto getEffectivePermissions(Long propertyId, String username) {
        UserModel user = findUser(username);
        PropertyModel property = findProperty(propertyId);

        // Owner/Manager: tudo liberado (counts = total de talhões)
        if (isOwner(user, property) || isManager(user, property)) {
            int totalPlots = plotRepository.findAllByProperty(property).size();

            return EffectivePermissionsResponseDto.builder()
                    .propertyId(property.getId())
                    .canManageProperty(true)
                    .canEditAllPlotsAnalyses(true)
                    .canEditAllPlotsCrops(true)
                    .plotsEditableAnalysesCount(totalPlots)
                    .plotsEditableCropsCount(totalPlots)
                    .build();
        }

        // Outros: precisa ter membership aprovado
        ensureApprovedMembership(user, property);

        EffectiveCalc calc = calculateEffective(property, user);

        return EffectivePermissionsResponseDto.builder()
                .propertyId(property.getId())
                .canManageProperty(false)
                .canEditAllPlotsAnalyses(calc.globalAnalyses)
                .canEditAllPlotsCrops(calc.globalCrops)
                .plotsEditableAnalysesCount(calc.analysesPlotIds.size())
                .plotsEditableCropsCount(calc.cropsPlotIds.size())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlotSummaryDto> getEditableAnalysesPlots(Long propertyId, String username) {
        return getEditablePlotsInternal(propertyId, username, true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlotSummaryDto> getEditableCropsPlots(Long propertyId, String username) {
        return getEditablePlotsInternal(propertyId, username, false);
    }

    /* ================= Relational helper ================= */

    private List<PlotSummaryDto> getEditablePlotsInternal(Long propertyId, String username, boolean analyses) {
        UserModel user = findUser(username);
        PropertyModel property = findProperty(propertyId);

        List<PlotModel> plots = plotRepository.findAllByProperty(property);

        // Owner/Manager: todos os plots
        if (isOwner(user, property) || isManager(user, property)) {
            return plots.stream().map(this::toPlotSummary).toList();
        }

        ensureApprovedMembership(user, property);

        EffectiveCalc calc = calculateEffective(property, user);

        // Global para o recurso -> retorna todos
        if (analyses && calc.globalAnalyses) {
            return plots.stream().map(this::toPlotSummary).toList();
        }
        if (!analyses && calc.globalCrops) {
            return plots.stream().map(this::toPlotSummary).toList();
        }

        Set<Long> allowedIds = analyses ? calc.analysesPlotIds : calc.cropsPlotIds;

        return plots.stream()
                .filter(p -> allowedIds.contains(p.getId()))
                .map(this::toPlotSummary)
                .toList();
    }

    /* ================= Core calc ================= */

    private EffectiveCalc calculateEffective(PropertyModel property, UserModel user) {
        // Busca todos requests do usuário na propriedade e filtra APPROVED
        List<PlotAccessRequestModel> approved = plotAccessRequestRepository
                .findAllByRequesterAndProperty(user, property)
                .stream()
                .filter(r -> r.getStatus() == AccessRequestStatus.APPROVED)
                .toList();

        boolean globalAnalyses = hasGlobal(approved, ANALYSES_ALLOWED);
        boolean globalCrops = hasGlobal(approved, CROPS_ALLOWED);

        // Carrega plots só se precisar de counts/listas por plot
        List<PlotModel> plots = plotRepository.findAllByProperty(property);
        Set<Long> allPlotIds = plots.stream().map(PlotModel::getId).collect(Collectors.toSet());

        // Se global, ids = todos (para counts / filtro)
        Set<Long> analysesIds = globalAnalyses ? allPlotIds : collectPlotIds(approved, ANALYSES_ALLOWED);
        Set<Long> cropsIds = globalCrops ? allPlotIds : collectPlotIds(approved, CROPS_ALLOWED);

        return new EffectiveCalc(globalAnalyses, globalCrops, analysesIds, cropsIds);
    }

    private boolean hasGlobal(List<PlotAccessRequestModel> approved, Set<PermissionType> allowed) {
        return approved.stream().anyMatch(r ->
                r.getScope() == PermissionScope.PROPERTY
                        && r.getPlot() == null
                        && r.getPermissionType() != null
                        && allowed.contains(r.getPermissionType())
        );
    }

    private Set<Long> collectPlotIds(List<PlotAccessRequestModel> approved, Set<PermissionType> allowed) {
        Set<Long> ids = new HashSet<>();
        for (PlotAccessRequestModel r : approved) {
            if (r.getScope() != PermissionScope.PLOT) continue;
            if (r.getPlot() == null || r.getPlot().getId() == null) continue;
            if (r.getPermissionType() == null) continue;
            if (!allowed.contains(r.getPermissionType())) continue;

            ids.add(r.getPlot().getId());
        }
        return ids;
    }

    private record EffectiveCalc(
            boolean globalAnalyses,
            boolean globalCrops,
            Set<Long> analysesPlotIds,
            Set<Long> cropsPlotIds
    ) {}

    /* ================= Helpers ================= */

    private PlotSummaryDto toPlotSummary(PlotModel plot) {
        String identification = (plot.getIdentification() == null) ? "" : plot.getIdentification();
        return new PlotSummaryDto(plot.getId(), identification);
    }

    private void ensureApprovedMembership(UserModel user, PropertyModel property) {
        boolean ok = propertyAccessRequestRepository
                .findByPropertyAndRequesterAndStatus(property, user, AccessRequestStatus.APPROVED)
                .isPresent();

        if (!ok) {
            throw new AccessDeniedException("Você não possui acesso aprovado a esta propriedade.");
        }
    }

    private boolean isOwner(UserModel user, PropertyModel property) {
        return property.getOwner() != null && Objects.equals(property.getOwner().getId(), user.getId());
    }

    private boolean isManager(UserModel user, PropertyModel property) {
        return property.getManager() != null && Objects.equals(property.getManager().getId(), user.getId());
    }

    private UserModel findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + username));
    }

    private PropertyModel findProperty(Long id) {
        return propertyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Propriedade não encontrada: " + id));
    }
}