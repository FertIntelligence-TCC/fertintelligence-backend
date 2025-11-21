package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.user.AccessRequestStatus;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.plotAccessRequest.PlotAccessRequestResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotAccessRequestModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.repository.PlotAccessRequestRepository;
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
    private PropertyAccessRequestRepository propertyAccessRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public PlotAccessRequestResponseDto requestAccess(Long propertyId, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);
        if (requester.getCargo() != Cargo.AGRONOMO_RESIDENTE) {
            throw new AccessDeniedException("Somente agrônomos residentes podem solicitar acesso aos talhões.");
        }

        PropertyModel property = findPropertyByIdOrThrow(propertyId);
        ensurePropertyAccessApproved(property, requester);
        ensurePropertyHasManager(property);

        plotAccessRequestRepository.findByPropertyAndRequesterAndStatus(property, requester, AccessRequestStatus.PENDING)
                .ifPresent(req -> {
                    throw new AccessDeniedException("Já existe uma solicitação pendente para esta propriedade.");
                });

        PlotAccessRequestModel accessRequest = PlotAccessRequestModel.builder()
                .property(property)
                .requester(requester)
                .status(AccessRequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        PlotAccessRequestModel saved = plotAccessRequestRepository.save(accessRequest);
        return saved.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlotAccessRequestResponseDto> getRequestsForManager(Long propertyId, String managerUsername) {
        PropertyModel property = findPropertyByIdOrThrow(propertyId);
        UserModel manager = findUserByUsernameOrThrow(managerUsername);
        checkManagerPermission(property, manager);

        return plotAccessRequestRepository.findAllByProperty(property).stream()
                .map(PlotAccessRequestModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PlotAccessRequestResponseDto decideRequest(Long requestId, boolean approve, String managerUsername) {
        UserModel manager = findUserByUsernameOrThrow(managerUsername);
        PlotAccessRequestModel request = plotAccessRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Solicitação de acesso a talhões não encontrada: " + requestId));

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

    private void ensurePropertyHasManager(PropertyModel property) {
        if (property.getManager() == null) {
            throw new AccessDeniedException("A propriedade ainda não possui um gerente definido.");
        }
    }

    private void ensurePropertyAccessApproved(PropertyModel property, UserModel requester) {
        propertyAccessRequestRepository.findByPropertyAndRequesterAndStatus(property, requester, AccessRequestStatus.APPROVED)
                .orElseThrow(() -> new AccessDeniedException("O proprietário ainda não aprovou o acesso a esta propriedade."));
    }

    private void checkManagerPermission(PropertyModel property, UserModel manager) {
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
}