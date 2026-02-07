package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.user.AccessRequestStatus;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.property.PropertyResponseDto;
import com.migueltcc.fertintelligence.dto.propertyAccessRequest.PropertyAccessRequestResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyAccessRequestModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.repository.PropertyAccessRequestRepository;
import com.migueltcc.fertintelligence.repository.PropertyRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.PropertyAccessRequestService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PropertyAccessRequestServiceImpl implements PropertyAccessRequestService {

    @Autowired
    private PropertyAccessRequestRepository propertyAccessRequestRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public PropertyAccessRequestResponseDto requestAccess(Long propertyId, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);
        PropertyModel property = findPropertyByIdOrThrow(propertyId);

        if (property.getOwner().getId().equals(requester.getId())) {
            throw new IllegalArgumentException("O proprietário já possui acesso à sua própria propriedade.");
        }

        boolean alreadyRequested = propertyAccessRequestRepository.findByPropertyAndRequesterAndStatus(
                property, requester, AccessRequestStatus.PENDING).isPresent();

        if (alreadyRequested) {
            throw new IllegalArgumentException("Já existe uma solicitação pendente para esta propriedade.");
        }

        // Verifica se já está aprovado
        boolean alreadyApproved = propertyAccessRequestRepository.findByPropertyAndRequesterAndStatus(
                property, requester, AccessRequestStatus.APPROVED).isPresent();
        if (alreadyApproved) {
            throw new IllegalArgumentException("Você já possui acesso aprovado a esta propriedade.");
        }

        PropertyAccessRequestModel request = PropertyAccessRequestModel.builder()
                .property(property)
                .requester(requester)
                .status(AccessRequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        return propertyAccessRequestRepository.save(request).toDto();
    }

    @Override
    public List<PropertyAccessRequestResponseDto> getRequestsForProperty(Long propertyId, String ownerUsername) {
        UserModel owner = findUserByUsernameOrThrow(ownerUsername);
        PropertyModel property = findPropertyByIdOrThrow(propertyId);

        checkOwnerPermission(property, owner);
        checkUserIsProprietario(owner);

        return propertyAccessRequestRepository.findAllByProperty(property)
                .stream()
                .map(PropertyAccessRequestModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public PropertyAccessRequestResponseDto decideRequest(Long requestId, boolean approve, String ownerUsername) {
        UserModel owner = findUserByUsernameOrThrow(ownerUsername);

        PropertyAccessRequestModel request = propertyAccessRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Solicitação não encontrada: " + requestId));

        checkOwnerPermission(request.getProperty(), owner);
        checkUserIsProprietario(owner);

        if (request.getStatus() != AccessRequestStatus.PENDING) {
            throw new IllegalArgumentException("Esta solicitação já foi processada.");
        }

        request.setStatus(approve ? AccessRequestStatus.APPROVED : AccessRequestStatus.REJECTED);
        PropertyAccessRequestModel savedRequest = propertyAccessRequestRepository.save(request);

        if (approve) {
            updatePropertyManagerIfNeeded(savedRequest);
        }

        return savedRequest.toDto();
    }

    // --- NOVA IMPLEMENTAÇÃO ---
    @Override
    public List<PropertyResponseDto> getApprovedPropertiesForUser(String username) {
        UserModel user = findUserByUsernameOrThrow(username);

        // Busca todas as solicitações deste usuário que foram APROVADAS
        List<PropertyAccessRequestModel> approvedRequests = propertyAccessRequestRepository
                .findAllByRequesterAndStatus(user, AccessRequestStatus.APPROVED);

        // Mapeia de Solicitação -> Propriedade -> PropertyResponseDto
        return approvedRequests.stream()
                .map(request -> request.getProperty().toDto())
                .collect(Collectors.toList());
    }
    // --------------------------

    private void updatePropertyManagerIfNeeded(PropertyAccessRequestModel request) {
        if (request.getRequester().getCargo() != Cargo.GERENTE) {
            return;
        }

        PropertyModel property = request.getProperty();
        // Se a propriedade não tem gerente, ou se a lógica de negócio permitir sobrescrever
        if (property.getManager() == null) {
            property.setManager(request.getRequester());
            propertyRepository.save(property);
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

    private void checkOwnerPermission(PropertyModel property, UserModel requestingUser) {
        if (!property.getOwner().getId().equals(requestingUser.getId())) {
            throw new AccessDeniedException("Você não tem permissão para gerenciar solicitações desta propriedade.");
        }
    }

    private void checkUserIsProprietario(UserModel user) {
        if (user.getCargo() != Cargo.PROPRIETARIO) {
            throw new AccessDeniedException("Apenas proprietários podem gerenciar solicitações de acesso.");
        }
    }
}