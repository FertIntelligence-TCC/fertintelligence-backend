package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.permissions.PermissionScope;
import com.migueltcc.fertintelligence.composedAttributes.permissions.PermissionType;
import com.migueltcc.fertintelligence.composedAttributes.user.AccessRequestStatus;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.repository.PlotAccessRequestRepository;
import com.migueltcc.fertintelligence.repository.PlotRepository;
import com.migueltcc.fertintelligence.repository.PropertyAccessRequestRepository;
import com.migueltcc.fertintelligence.repository.PropertyRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;

@Component
@RequiredArgsConstructor
public class PermissionManager {

    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final PlotRepository plotRepository;

    // owner aprova entrada na propriedade
    private final PropertyAccessRequestRepository propertyAccessRequestRepository;

    // gerente aprova permissão de edição
    private final PlotAccessRequestRepository plotAccessRequestRepository;

    private static final EnumSet<PermissionType> ANALYSES_ALLOWED =
            EnumSet.of(PermissionType.EDIT_ANALYSES, PermissionType.EDIT_ANALYSES_AND_CROPS);

    private static final EnumSet<PermissionType> CROPS_ALLOWED =
            EnumSet.of(PermissionType.EDIT_CROPS, PermissionType.EDIT_ANALYSES_AND_CROPS);

    /* ======================================================
       MANAGE (Property) - novo (Etapa 5+)
    ====================================================== */

    @Transactional(readOnly = true)
    public void assertCanManageProperty(Long propertyId, String username) {
        if (!canManageProperty(propertyId, username)) {
            throw new AccessDeniedException("Você não tem permissão para gerenciar esta propriedade.");
        }
    }

    @Transactional(readOnly = true)
    public boolean canManageProperty(Long propertyId, String username) {
        UserModel user = findUser(username);
        PropertyModel property = findProperty(propertyId);
        return isSupreme(user) || isOwner(user, property) || isManager(user, property);
    }

    @Transactional(readOnly = true)
    public void assertCanManageProperty(PropertyModel property, UserModel user) {
        if (!(isSupreme(user) || isOwner(user, property) || isManager(user, property))) {
            throw new AccessDeniedException("Você não tem permissão para gerenciar esta propriedade.");
        }
    }

    /* ======================================================
       READ (Plot) - API pública estável
    ====================================================== */

    @Transactional(readOnly = true)
    public void assertCanReadPlot(PlotModel plot, UserModel user) {
        if (!canReadPlot(plot, user)) {
            throw new AccessDeniedException("Você não tem permissão para visualizar recursos deste talhão.");
        }
    }

    // Compat: alguns services antigos chamam assertCanRead(plot, user)
    @Transactional(readOnly = true)
    public void assertCanRead(PlotModel plot, UserModel user) {
        assertCanReadPlot(plot, user);
    }

    @Transactional(readOnly = true)
    public boolean canReadPlot(PlotModel plot, UserModel user) {
        if (plot == null || plot.getProperty() == null || user == null) return false;

        PropertyModel property = plot.getProperty();

        // owner/manager: leitura total
        if (isSupreme(user) || isOwner(user, property) || isManager(user, property)) return true;

        // demais: precisa ter entrada aprovada na propriedade
        return hasApprovedPropertyMembership(user, property);
    }

    @Transactional(readOnly = true)
    public void assertCanGenerateRecommendation(PropertyModel property, PlotModel plot, UserModel user) {
        if (property == null || plot == null || user == null) {
            throw new AccessDeniedException("Você não tem permissão para gerar recomendações neste talhão.");
        }

        if (plot.getProperty() == null || !plot.getProperty().getId().equals(property.getId())) {
            throw new AccessDeniedException("Você não tem permissão para gerar recomendações neste talhão.");
        }

        if (isSupreme(user) || isOwner(user, property) || isManager(user, property)) return;
        if (!hasApprovedPropertyMembership(user, property)) {
            throw new AccessDeniedException("Você não tem permissão para gerar recomendações neste talhão.");
        }

        // Se usuário tiver delegação em nível de propriedade, pode gerar em qualquer talhão da propriedade.
        if (plotAccessRequestRepository.existsByPropertyAndRequesterAndScopeAndStatus(
                property, user, PermissionScope.PROPERTY, AccessRequestStatus.APPROVED
        )) {
            return;
        }

        // Para acessos dependentes de talhão, exige permissão aprovada no talhão selecionado.
        if (!plotAccessRequestRepository.existsByPropertyAndPlotAndRequesterAndScopeAndStatus(
                property, plot, user, PermissionScope.PLOT, AccessRequestStatus.APPROVED
        )) {
            throw new AccessDeniedException("Você não tem permissão para gerar recomendações neste talhão.");
        }
    }

    @Transactional(readOnly = true)
    public void assertCanPrintRecommendation(UserModel user) {
        if (user == null || user.getCargo() == null ||
                (user.getCargo() != Cargo.AGRONOMO_RESIDENTE && user.getCargo() != Cargo.AGRONOMO_CONSULTOR)) {
            throw new AccessDeniedException("Somente agrônomos residentes e consultores podem imprimir recomendações formais.");
        }
    }

    /* ======================================================
       WRITE genérico (compatibilidade)
       - Use isso quando o recurso for "de análises"
       - Para culturas, use assertCanEditCrops.
    ====================================================== */

    // Compat: muitos services usam assertCanWrite(plot, user)
    @Transactional(readOnly = true)
    public void assertCanWrite(PlotModel plot, UserModel user) {
        assertCanEditAnalyses(plot.getProperty(), plot, user);
    }

    /* ======================================================
       EDIT por recurso (recomendado)
    ====================================================== */

    @Transactional(readOnly = true)
    public void assertCanEditAnalyses(PropertyModel property, PlotModel plot, UserModel user) {
        if (!hasEditPermission(property, plot, user, ANALYSES_ALLOWED)) {
            throw new AccessDeniedException("Você não tem permissão para editar análises neste talhão.");
        }
    }

    @Transactional(readOnly = true)
    public void assertCanEditCrops(PropertyModel property, PlotModel plot, UserModel user) {
        if (!hasEditPermission(property, plot, user, CROPS_ALLOWED)) {
            throw new AccessDeniedException("Você não tem permissão para editar culturas neste talhão.");
        }
    }

    /* ======================================================
       API por ids/username (compatibilidade)
    ====================================================== */

    @Transactional(readOnly = true)
    public void assertCanEditAnalyses(Long propertyId, Long plotId, String username) {
        if (!canEditAnalyses(propertyId, plotId, username)) {
            throw new AccessDeniedException("Você não tem permissão para editar análises neste talhão.");
        }
    }

    @Transactional(readOnly = true)
    public void assertCanEditCrops(Long propertyId, Long plotId, String username) {
        if (!canEditCrops(propertyId, plotId, username)) {
            throw new AccessDeniedException("Você não tem permissão para editar culturas neste talhão.");
        }
    }

    @Transactional(readOnly = true)
    public boolean canEditAnalyses(Long propertyId, Long plotId, String username) {
        return hasEditPermission(propertyId, plotId, username, ANALYSES_ALLOWED);
    }

    @Transactional(readOnly = true)
    public boolean canEditCrops(Long propertyId, Long plotId, String username) {
        return hasEditPermission(propertyId, plotId, username, CROPS_ALLOWED);
    }

    /* ======================================================
       Core rules
    ====================================================== */

    private boolean hasEditPermission(Long propertyId, Long plotId, String username, EnumSet<PermissionType> allowedTypes) {
        UserModel user = findUser(username);
        PropertyModel property = findProperty(propertyId);

        // melhoria: valida plotId + propertyId no próprio finder (evita “plot de outra property”)
        PlotModel plot = (plotId == null) ? null : findPlotInProperty(plotId, propertyId);

        return hasEditPermission(property, plot, user, allowedTypes);
    }

    private boolean hasEditPermission(PropertyModel property, PlotModel plot, UserModel user, EnumSet<PermissionType> allowedTypes) {
        if (property == null || user == null) return false;

        // 1) Owner/Manager fazem tudo
        if (isSupreme(user) || isOwner(user, property) || isManager(user, property)) return true;

        // 2) Precisa ter entrada aprovada na propriedade
        if (!hasApprovedPropertyMembership(user, property)) return false;

        // 3) Se plot informado, já foi garantido que pertence à property no finder por id+propertyId
        // (mantém proteção caso venha plot “montado” de outro lugar)
        if (plot != null && (plot.getProperty() == null || !plot.getProperty().getId().equals(property.getId()))) return false;

        // 4) Primeiro: escopo PROPERTY (permite editar tudo sem plot específico)
        if (hasApprovedDelegatedAccess(property, null, user, PermissionScope.PROPERTY, allowedTypes)) {
            return true;
        }

        // 5) Depois: escopo PLOT (precisa de plot)
        if (plot == null) return false;

        return hasApprovedDelegatedAccess(property, plot, user, PermissionScope.PLOT, allowedTypes);
    }

    /**
     * melhoria: 1 query só com permissionType IN (...)
     */
    private boolean hasApprovedDelegatedAccess(
            PropertyModel property,
            PlotModel plotOrNull,
            UserModel user,
            PermissionScope scope,
            EnumSet<PermissionType> allowedTypes
    ) {
        if (scope == PermissionScope.PROPERTY) {
            return plotAccessRequestRepository
                    .existsByPropertyAndRequesterAndScopeAndPermissionTypeInAndStatus(
                            property, user, scope, allowedTypes, AccessRequestStatus.APPROVED
                    );
        }

        // scope == PLOT
        if (plotOrNull == null) return false;

        return plotAccessRequestRepository
                .existsByPropertyAndPlotAndRequesterAndScopeAndPermissionTypeInAndStatus(
                        property, plotOrNull, user, scope, allowedTypes, AccessRequestStatus.APPROVED
                );
    }

    private boolean hasApprovedPropertyMembership(UserModel user, PropertyModel property) {
        return propertyAccessRequestRepository
                .findByPropertyAndRequesterAndStatus(property, user, AccessRequestStatus.APPROVED)
                .isPresent();
    }

    private boolean isSupreme(UserModel user) {
        return user != null && user.getCargo() == Cargo.USUARIO_SUPREMO;
    }

    private boolean isOwner(UserModel user, PropertyModel property) {
        return property.getOwner() != null && property.getOwner().getId().equals(user.getId());
    }

    private boolean isManager(UserModel user, PropertyModel property) {
        return property.getManager() != null && property.getManager().getId().equals(user.getId());
    }

    /* ======================================================
       Finders (centraliza exceptions)
    ====================================================== */

    private UserModel findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + username));
    }

    private PropertyModel findProperty(Long propertyId) {
        return propertyRepository.findById(propertyId)
                .orElseThrow(() -> new EntityNotFoundException("Propriedade não encontrada: " + propertyId));
    }

    /**
     * melhoria: evita buscar um plot que não pertença à propriedade.
     */
    private PlotModel findPlotInProperty(Long plotId, Long propertyId) {
        return plotRepository.findByIdAndPropertyId(plotId, propertyId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Talhão não encontrado: " + plotId + " (na propriedade " + propertyId + ")"
                ));
    }
}
