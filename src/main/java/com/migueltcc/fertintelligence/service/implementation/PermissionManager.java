package com.migueltcc.fertintelligence.security;

import com.migueltcc.fertintelligence.composedAttributes.user.AccessRequestStatus;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.repository.PlotAccessRequestRepository;
import com.migueltcc.fertintelligence.repository.PropertyAccessRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PermissionManager {

    private final PropertyAccessRequestRepository propertyAccessRequestRepository;
    private final PlotAccessRequestRepository plotAccessRequestRepository;

    // ========= API PRINCIPAL (padronizada) =========

    public void assertCanRead(PlotModel plot, UserModel user) {
        if (!canRead(plot, user)) {
            throw new AccessDeniedException("Você não tem permissão para visualizar este recurso.");
        }
    }

    public void assertCanWrite(PlotModel plot, UserModel user) {
        if (!canWrite(plot, user)) {
            throw new AccessDeniedException("Você não tem permissão para criar/editar/remover este recurso.");
        }
    }

    public boolean canRead(PlotModel plot, UserModel user) {
        validateInputs(plot, user);

        PropertyModel property = plot.getProperty();

        // Owner/Manager da propriedade sempre leem tudo
        if (isPropertyOwner(property, user) || isPropertyManager(property, user)) {
            return true;
        }

        // Qualquer cargo aprovado na PROPRIEDADE pode ler tudo daquela propriedade (preferencial)
        if (hasApprovedPropertyAccess(property, user)) {
            return true;
        }

        // Fallback (caso ainda exista permissão por talhão)
        return hasApprovedPlotAccess(plot, user);
    }

    public boolean canWrite(PlotModel plot, UserModel user) {
        validateInputs(plot, user);

        PropertyModel property = plot.getProperty();

        // Somente PROPRIETARIO/GERENTE e tem que ser owner/manager daquela propriedade
        boolean isCrudRole = user.getCargo() == Cargo.PROPRIETARIO || user.getCargo() == Cargo.GERENTE;
        if (!isCrudRole) return false;

        return isPropertyOwner(property, user) || isPropertyManager(property, user);
    }

    // ========= COMPAT (para código antigo) =========
    // Mantém compatibilidade caso outros serviços ainda chamem esses nomes.
    public void assertCanReadPlot(PlotModel plot, UserModel user) { assertCanRead(plot, user); }
    public void assertCanWritePlot(PlotModel plot, UserModel user) { assertCanWrite(plot, user); }

    // ========= Internos =========

    private void validateInputs(PlotModel plot, UserModel user) {
        if (plot == null || plot.getProperty() == null) {
            throw new IllegalArgumentException("Plot/Property não pode ser nulo.");
        }
        if (user == null || user.getCargo() == null) {
            throw new AccessDeniedException("Usuário inválido.");
        }
    }

    private boolean isPropertyOwner(PropertyModel property, UserModel user) {
        return property.getOwner() != null && property.getOwner().getId().equals(user.getId());
    }

    private boolean isPropertyManager(PropertyModel property, UserModel user) {
        return property.getManager() != null && property.getManager().getId().equals(user.getId());
    }

    private boolean hasApprovedPropertyAccess(PropertyModel property, UserModel user) {
        return propertyAccessRequestRepository
                .findByPropertyAndRequesterAndStatus(property, user, AccessRequestStatus.APPROVED)
                .isPresent();
    }

    private boolean hasApprovedPlotAccess(PlotModel plot, UserModel user) {
        return plotAccessRequestRepository
                .findByPlotAndRequesterAndStatus(plot, user, AccessRequestStatus.APPROVED)
                .isPresent();
    }
}