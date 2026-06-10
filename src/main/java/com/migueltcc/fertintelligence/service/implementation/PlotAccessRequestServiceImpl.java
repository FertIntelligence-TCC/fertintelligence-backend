package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.permissions.PermissionScope;
import com.migueltcc.fertintelligence.composedAttributes.permissions.PermissionType;
import com.migueltcc.fertintelligence.composedAttributes.user.AccessRequestStatus;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.plotAccessRequest.PlotAccessRequestResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotAccessRequestModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.repository.PlotAccessRequestRepository;
import com.migueltcc.fertintelligence.repository.PlotRepository;
import com.migueltcc.fertintelligence.repository.PropertyAccessRequestRepository;
import com.migueltcc.fertintelligence.repository.PropertyRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.PlotAccessRequestService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlotAccessRequestServiceImpl implements PlotAccessRequestService {

    @Autowired
    private PlotAccessRequestRepository plotAccessRequestRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private PlotRepository plotRepository;

    @Autowired
    private PropertyAccessRequestRepository propertyAccessRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public PlotAccessRequestResponseDto requestAccess(Long propertyId, Long plotId, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);

        PropertyModel property = findPropertyByIdOrThrow(propertyId);
        ensurePropertyAccessApproved(property, requester);
        ensurePropertyHasManager(property);

        PlotModel plot = null;
        PermissionScope scope;
        PermissionType permissionType;

        // Distribuição das permissões de acordo com o Cargo
        if (requester.getCargo() == Cargo.AGRONOMO_RESIDENTE) {
            // Residente tem permissão na propriedade inteira, plotId fica null
            scope = PermissionScope.PROPERTY;
            permissionType = PermissionType.EDIT_ANALYSES_AND_CROPS;

        } else if (requester.getCargo() == Cargo.AGRONOMO_CONSULTOR) {
            if (plotId == null) throw new IllegalArgumentException("O talhão deve ser informado para o Agrônomo Consultor.");
            plot = findPlotByIdOrThrow(plotId);
            ensurePlotBelongsToProperty(property, plot);
            scope = PermissionScope.PLOT;
            permissionType = PermissionType.EDIT_ANALYSES_AND_CROPS;

        } else if (requester.getCargo() == Cargo.SECRETARIO) {
            if (plotId == null) throw new IllegalArgumentException("O talhão deve ser informado para o Secretário.");
            plot = findPlotByIdOrThrow(plotId);
            ensurePlotBelongsToProperty(property, plot);
            scope = PermissionScope.PLOT;
            // Secretário apenas edita as análises
            permissionType = PermissionType.EDIT_ANALYSES;

        } else {
            throw new AccessDeniedException("Somente agrônomos consultores, residentes ou secretários podem solicitar este tipo de acesso.");
        }

        // Verifica pendência (usa o método ajustado do repositório)
        plotAccessRequestRepository.findByPropertyAndPlotAndRequesterAndStatus(
                property, plot, requester, AccessRequestStatus.PENDING
        ).ifPresent(req -> {
            throw new AccessDeniedException("Já existe uma solicitação pendente para este alvo.");
        });

        PlotAccessRequestModel accessRequest = PlotAccessRequestModel.builder()
                .property(property)
                .plot(plot)
                .requester(requester)
                .scope(scope)
                .permissionType(permissionType)
                .status(AccessRequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        PlotAccessRequestModel saved = plotAccessRequestRepository.save(accessRequest);
        return saved.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlotAccessRequestResponseDto> getRequestsForManager(Long propertyId, AccessRequestStatus status, String username) {
        PropertyModel property = findPropertyByIdOrThrow(propertyId);
        UserModel user = findUserByUsernameOrThrow(username);

        boolean isManager = property.getManager() != null && property.getManager().getId().equals(user.getId());

        return plotAccessRequestRepository.findAllByProperty(property).stream()
                .filter(req -> status == null || req.getStatus() == status)
                .filter(req -> isManager || req.getRequester().getId().equals(user.getId()))
                .map(PlotAccessRequestModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PlotAccessRequestResponseDto decideRequest(Long requestId, boolean approve, String managerUsername) {
        UserModel manager = findUserByUsernameOrThrow(managerUsername);

        PlotAccessRequestModel request = plotAccessRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Solicitação não encontrada."));

        checkManagerPermission(request.getProperty(), manager);

        if (request.getStatus() != AccessRequestStatus.PENDING) {
            throw new AccessDeniedException("A solicitação já foi processada.");
        }

        if (approve) {
            request.setStatus(AccessRequestStatus.APPROVED);
            PlotAccessRequestModel saved = plotAccessRequestRepository.save(request);
            return saved.toDto();
        }

        request.setStatus(AccessRequestStatus.REJECTED);
        PlotAccessRequestResponseDto response = request.toDto();
        plotAccessRequestRepository.delete(request);
        return response;
    }

    @Override
    @Transactional
    public PlotAccessRequestResponseDto revokeRequest(Long requestId, String username) {
        UserModel user = findUserByUsernameOrThrow(username);

        PlotAccessRequestModel request = plotAccessRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Solicitação não encontrada."));

        boolean isManager = request.getProperty().getManager() != null && request.getProperty().getManager().getId().equals(user.getId());
        boolean isRequester = request.getRequester().getId().equals(user.getId());

        if (!isManager && !isRequester) {
            throw new AccessDeniedException("Você não tem permissão para revogar ou cancelar esta solicitação.");
        }

        PlotAccessRequestResponseDto responseDto = request.toDto();

        plotAccessRequestRepository.delete(request);

        return responseDto;
    }

    private void ensurePropertyHasManager(PropertyModel property) {
        if (property.getManager() == null) {
            throw new AccessDeniedException("A propriedade ainda não possui um gerente definido.");
        }
    }

    private void ensurePlotBelongsToProperty(PropertyModel property, PlotModel plot) {
        if (!plot.getProperty().getId().equals(property.getId())) {
            throw new AccessDeniedException("O talhão selecionado não pertence à propriedade informada.");
        }
    }

    private void ensurePropertyAccessApproved(PropertyModel property, UserModel requester) {
        propertyAccessRequestRepository.findByPropertyAndRequesterAndStatus(
                        property, requester, AccessRequestStatus.APPROVED
                )
                .orElseThrow(() ->
                        new AccessDeniedException("O proprietário ainda não aprovou o acesso a esta propriedade.")
                );
    }

    private void checkManagerPermission(PropertyModel property, UserModel manager) {
        if (manager.getCargo() == Cargo.USUARIO_SUPREMO) {
            return;
        }

        if (property.getManager() == null || !property.getManager().getId().equals(manager.getId())) {
            throw new AccessDeniedException("Apenas o gerente da propriedade pode gerenciar essas solicitações.");
        }
    }

    private UserModel findUserByUsernameOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + username));
    }

    private PropertyModel findPropertyByIdOrThrow(Long propertyId) {
        return propertyRepository.findById(propertyId)
                .orElseThrow(() -> new EntityNotFoundException("Propriedade não encontrada: " + propertyId));
    }

    private PlotModel findPlotByIdOrThrow(Long plotId) {
        return plotRepository.findById(plotId)
                .orElseThrow(() -> new EntityNotFoundException("Talhão não encontrado: " + plotId));
    }
}