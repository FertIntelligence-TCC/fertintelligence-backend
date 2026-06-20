package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.user.AccessRequestStatus;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.property.PropertyResponseDto;
import com.migueltcc.fertintelligence.dto.propertyAccessRequest.PropertyAccessRequestResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.PlotAccessRequestModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyAccessRequestModel;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.repository.PropertyAccessRequestRepository;
import com.migueltcc.fertintelligence.repository.PlotAccessRequestRepository;
import com.migueltcc.fertintelligence.repository.PropertyRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.PropertyAccessRequestService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PropertyAccessRequestServiceImpl implements PropertyAccessRequestService {

    @Autowired
    private PropertyAccessRequestRepository propertyAccessRequestRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private PlotAccessRequestRepository plotAccessRequestRepository;

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
        UserModel user = findUserByUsernameOrThrow(ownerUsername);
        PropertyModel property = findPropertyByIdOrThrow(propertyId);

        // Valida se o usuário logado é Dono ou Gerente
        checkManagementPermission(property, user);

        boolean isOwner = property.getOwner().getId().equals(user.getId());

        return propertyAccessRequestRepository.findAllByProperty(property)
                .stream()
                .filter(request -> {
                    // Se o usuário logado for o dono, ele vê todas as solicitações (para poder revogar o gerente)
                    if (isOwner) return true;

                    // Se for o gerente, ele NÃO deve ver solicitações de gerentes ou do próprio dono
                    return request.getRequester().getCargo() != Cargo.GERENTE &&
                            request.getRequester().getCargo() != Cargo.PROPRIETARIO;
                })
                .map(PropertyAccessRequestModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PropertyAccessRequestResponseDto decideRequest(Long requestId, boolean approve, String ownerUsername) {
        PropertyAccessRequestModel request = propertyAccessRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Solicitação não encontrada"));

        UserModel user = findUserByUsernameOrThrow(ownerUsername);

        // Validação de segurança: verificar se quem decide é o dono ou o gerente
        checkManagementPermission(request.getProperty(), user);

        // Trava de segurança: Impede que um Gerente aprove, recuse ou revogue outro Gerente
        boolean isOwner = request.getProperty().getOwner().getId().equals(user.getId());
        if (!isOwner && request.getRequester().getCargo() == Cargo.GERENTE) {
            throw new AccessDeniedException("Apenas o proprietário pode gerenciar o acesso de um gerente.");
        }

        if (approve) {
            request.setStatus(AccessRequestStatus.APPROVED);
            // Se quem está sendo aprovado é GERENTE, vincula na propriedade (se possível)
            updatePropertyManagerIfNeeded(request);
            return propertyAccessRequestRepository.save(request).toDto();
        }
        else {
            // Se rejeitado/revogado, remove do banco de dados
            propertyAccessRequestRepository.delete(request);

            // Retorna o DTO com status REJECTED apenas para o frontend saber o que aconteceu
            PropertyAccessRequestResponseDto response = request.toDto();
            response.setStatus(AccessRequestStatus.REJECTED);
            return response;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PropertyResponseDto> getApprovedPropertiesForUser(String username) {
        UserModel user = findUserByUsernameOrThrow(username);

        Map<Long, PropertyModel> approvedPropertiesById = new LinkedHashMap<>();

        // Vínculo aprovado em nível de propriedade: residentes e secretários/consultores
        // podem ter esse vínculo como etapa anterior à aprovação por talhão.
        propertyAccessRequestRepository
                .findAllByRequesterAndStatus(user, AccessRequestStatus.APPROVED)
                .stream()
                .map(PropertyAccessRequestModel::getProperty)
                .forEach(property -> approvedPropertiesById.putIfAbsent(property.getId(), property));

        // Vínculo aprovado em nível de talhão: consultores e secretários podem ter
        // permissões efetivas apenas em talhões específicos. A lista de propriedades
        // para seleção deve ser derivada dessas aprovações, sem persistir duplicatas.
        plotAccessRequestRepository
                .findAllByRequesterAndStatus(user, AccessRequestStatus.APPROVED)
                .stream()
                .map(PlotAccessRequestModel::getProperty)
                .forEach(property -> approvedPropertiesById.putIfAbsent(property.getId(), property));

        return approvedPropertiesById.values()
                .stream()
                .map(PropertyModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PropertyAccessRequestResponseDto> getReceivedRequests(String ownerUsername) {
        return propertyAccessRequestRepository.findAllByProperty_Owner_Username(ownerUsername)
                .stream()
                .map(PropertyAccessRequestModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void revokeAccess(Long propertyId, String username) {
        PropertyAccessRequestModel request = propertyAccessRequestRepository
                .findByPropertyIdAndRequesterUsernameAndStatus(propertyId, username, AccessRequestStatus.APPROVED)
                .orElseThrow(() -> new EntityNotFoundException("Vínculo de acesso não encontrado para esta propriedade."));

        // Se o solicitante for um GERENTE, removemos ele do cargo de gerente na propriedade ao revogar
        if (request.getRequester().getCargo() == Cargo.GERENTE) {
            PropertyModel property = request.getProperty();
            if (property.getManager() != null && property.getManager().getUsername().equals(username)) {
                property.setManager(null);
                propertyRepository.save(property);
            }
        }

        propertyAccessRequestRepository.delete(request);
    }

    @Override
    public boolean hasAccessToProperty(Long propertyId, String username) {
        // 1. Verifica se o usuário é o dono
        PropertyModel property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new EntityNotFoundException("Propriedade não encontrada"));

        if (property.getOwner().getUsername().equals(username)) {
            return true;
        }

        // 2. Verifica se existe uma solicitação com status APPROVED
        return propertyAccessRequestRepository.findByPropertyIdAndRequesterUsernameAndStatus(
                propertyId, username, AccessRequestStatus.APPROVED
        ).isPresent();
    }

    @Override
    @Transactional
    public void leaveProperty(Long propertyId, String username) {

        PropertyModel property = findPropertyByIdOrThrow(propertyId);

        // Owner não pode "sair" da própria propriedade
        if (property.getOwner() != null && property.getOwner().getUsername().equals(username)) {
            throw new AccessDeniedException("O proprietário não pode se desvincular da própria propriedade.");
        }

        // 1) Se tiver vínculo aprovado, revoga (já remove gerente se for o caso)
        boolean hasApproved = propertyAccessRequestRepository
                .findByPropertyIdAndRequesterUsernameAndStatus(propertyId, username, AccessRequestStatus.APPROVED)
                .isPresent();

        if (hasApproved) {
            revokeAccess(propertyId, username);
            return;
        }

        // 2) Se tiver solicitação pendente, cancela (deleta)
        propertyAccessRequestRepository
                .findByPropertyIdAndRequesterUsernameAndStatus(propertyId, username, AccessRequestStatus.PENDING)
                .ifPresentOrElse(
                        propertyAccessRequestRepository::delete,
                        () -> { throw new EntityNotFoundException("Nenhum vínculo ou solicitação encontrada para esta propriedade."); }
                );
    }
    // --------------------------

    private void updatePropertyManagerIfNeeded(PropertyAccessRequestModel request) {
        if (request.getRequester().getCargo() != Cargo.GERENTE) return;

        PropertyModel property = request.getProperty();

        // já tem gerente diferente
        if (property.getManager() != null &&
                !property.getManager().getId().equals(request.getRequester().getId())) {
            throw new AccessDeniedException("A propriedade já possui um gerente.");
        }

        // seta (se null ou se for o mesmo usuário)
        property.setManager(request.getRequester());
        propertyRepository.save(property);
    }

    private UserModel findUserByUsernameOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + username));
    }

    private PropertyModel findPropertyByIdOrThrow(Long propertyId) {
        return propertyRepository.findById(propertyId)
                .orElseThrow(() -> new EntityNotFoundException("Propriedade não encontrada: " + propertyId));
    }

    // Substitua os métodos checkOwnerPermission e checkUserIsProprietario por este novo:
    private void checkManagementPermission(PropertyModel property, UserModel requestingUser) {

        boolean isOwner = property.getOwner().getId().equals(requestingUser.getId());
        boolean isManager = property.getManager() != null && property.getManager().getId().equals(requestingUser.getId());

        if (!isOwner && !isManager) {
            throw new AccessDeniedException("Você não tem permissão para gerenciar solicitações desta propriedade. Apenas o proprietário ou o gerente podem realizar esta ação.");
        }
    }
}