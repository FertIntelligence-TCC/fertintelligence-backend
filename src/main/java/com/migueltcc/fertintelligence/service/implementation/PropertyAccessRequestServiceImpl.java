package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.user.AccessRequestStatus;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
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
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public PropertyAccessRequestResponseDto requestAccess(Long propertyId, String username) {
        UserModel requester = findUserByUsernameOrThrow(username);

        if (requester.getCargo() == Cargo.PROPRIETARIO) {
            throw new AccessDeniedException("Proprietários já possuem acesso às propriedades.");
        }

        PropertyModel property = findPropertyByIdOrThrow(propertyId);

        if (property.getOwner().getId().equals(requester.getId())) {
            throw new AccessDeniedException("Você já é proprietário desta propriedade.");
        }

        propertyAccessRequestRepository.findByPropertyAndRequesterAndStatus(property, requester, AccessRequestStatus.PENDING)
                .ifPresent(req -> {
                    throw new AccessDeniedException("Já existe uma solicitação pendente para esta propriedade.");
                });

        PropertyAccessRequestModel accessRequest = PropertyAccessRequestModel.builder()
                .property(property)
                .requester(requester)
                .status(AccessRequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        PropertyAccessRequestModel saved = propertyAccessRequestRepository.save(accessRequest);
        return saved.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PropertyAccessRequestResponseDto> getRequestsForProperty(Long propertyId, String ownerUsername) {
        UserModel owner = findUserByUsernameOrThrow(ownerUsername);
        checkUserIsProprietario(owner);

        PropertyModel property = findPropertyByIdOrThrow(propertyId);
        checkOwnerPermission(property, owner);

        return propertyAccessRequestRepository.findAllByProperty(property).stream()
                .map(PropertyAccessRequestModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PropertyAccessRequestResponseDto decideRequest(Long requestId, boolean approve, String ownerUsername) {
        UserModel owner = findUserByUsernameOrThrow(ownerUsername);
        checkUserIsProprietario(owner);

        PropertyAccessRequestModel request = propertyAccessRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Solicitação não encontrada: " + requestId));

        checkOwnerPermission(request.getProperty(), owner);

        if (request.getStatus() != AccessRequestStatus.PENDING) {
            throw new AccessDeniedException("A solicitação já foi processada.");
        }

        if (approve) {
            request.setStatus(AccessRequestStatus.APPROVED);
            PropertyAccessRequestModel saved = propertyAccessRequestRepository.save(request);
            return saved.toDto();
        }

        request.setStatus(AccessRequestStatus.REJECTED);
        PropertyAccessRequestResponseDto response = request.toDto();
        propertyAccessRequestRepository.delete(request);
        return response;
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