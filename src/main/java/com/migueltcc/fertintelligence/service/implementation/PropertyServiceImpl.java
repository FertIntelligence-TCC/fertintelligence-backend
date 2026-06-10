package com.migueltcc.fertintelligence.service.implementation;

import com.migueltcc.fertintelligence.composedAttributes.property.Localizacao;
import com.migueltcc.fertintelligence.composedAttributes.user.AccessRequestStatus;
import com.migueltcc.fertintelligence.composedAttributes.user.Cargo;
import com.migueltcc.fertintelligence.dto.property.PropertyCreateRequestDto;
import com.migueltcc.fertintelligence.dto.property.PropertyPostRequestDto;
import com.migueltcc.fertintelligence.dto.property.PropertyResponseDto;
import com.migueltcc.fertintelligence.model.fertintelligence.PropertyModel;
import com.migueltcc.fertintelligence.model.fertintelligence.UserModel;
import com.migueltcc.fertintelligence.repository.PropertyAccessRequestRepository;
import com.migueltcc.fertintelligence.repository.PropertyRepository;
import com.migueltcc.fertintelligence.repository.UserRepository;
import com.migueltcc.fertintelligence.service.documentation.PropertyService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PropertyServiceImpl implements PropertyService {

    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final PropertyAccessRequestRepository propertyAccessRequestRepository;

    /* ======================================================
       CREATE
    ====================================================== */

    @Override
    @Transactional
    public PropertyResponseDto createProperty(PropertyCreateRequestDto dto, String username) {

        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserIsProprietario(owner);

        String nome = dto.getNome().trim();
        String endereco = dto.getEndereco().trim();
        String cnpj = sanitizeCnpj(dto.getCnpj());

        validateUniqueNome(nome);
        validateUniqueCnpj(cnpj);

        PropertyModel property = PropertyModel.builder()
                .nome(nome)
                .endereco(endereco)
                .cnpj(cnpj)
                .localizacao(buildLocalizacao(dto.getLocalizacao()))
                .idFoto(dto.getIdFoto())
                .owner(owner)
                .build();

        return propertyRepository.save(property).toDto();
    }

    /* ======================================================
       READ
    ====================================================== */

    @Override
    @Transactional(readOnly = true)
    public PropertyResponseDto getPropertyById(Long propertyId, String username) {

        UserModel user = findUserByUsernameOrThrow(username);
        PropertyModel property = findPropertyByIdOrThrow(propertyId);

        checkPropertyPermission(property, user);

        return property.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PropertyResponseDto> getAllPropertiesByOwner(String username) {

        UserModel owner = findUserByUsernameOrThrow(username);
        checkUserIsProprietarioOrSupreme(owner);

        return propertyRepository.findAllByOwner(owner)
                .stream()
                .map(PropertyModel::toDto)
                .collect(Collectors.toList());
    }

    /* ======================================================
       UPDATE
    ====================================================== */

    @Override
    @Transactional
    public PropertyResponseDto updateProperty(Long propertyId,
                                              PropertyPostRequestDto dto,
                                              String username) {

        UserModel user = findUserByUsernameOrThrow(username);
        PropertyModel property = findPropertyByIdOrThrow(propertyId);

        checkPropertyPermission(property, user);

        updateNomeIfNecessary(property, dto, propertyId);
        updateCnpjIfNecessary(property, dto, propertyId);
        updateEnderecoIfNecessary(property, dto);
        updateLocalizacaoIfNecessary(property, dto);
        updateIdFotoIfNecessary(property, dto);

        return propertyRepository.save(property).toDto();
    }

    /* ======================================================
       DELETE
    ====================================================== */

    @Override
    @Transactional
    public void deleteProperty(Long propertyId, String username) {

        UserModel user = findUserByUsernameOrThrow(username);
        PropertyModel property = findPropertyByIdOrThrow(propertyId);

        checkPropertyPermission(property, user);

        propertyRepository.delete(property);
    }

    /* ======================================================
       SEARCH
    ====================================================== */

    @Override
    @Transactional(readOnly = true)
    public List<PropertyResponseDto> searchPropertiesByName(String nome, String username) {

        findUserByUsernameOrThrow(username);

        return propertyRepository
                .findByNomeContainingIgnoreCase(nome)
                .stream()
                .map(PropertyModel::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PropertyResponseDto> getManageableProperties(String username) {

        UserModel user = findUserByUsernameOrThrow(username);

        if (user.getCargo() == Cargo.PROPRIETARIO || user.getCargo() == Cargo.USUARIO_SUPREMO) {
            return propertyRepository.findAllByOwner(user)
                    .stream()
                    .map(PropertyModel::toDto)
                    .collect(Collectors.toList());
        }

        if (user.getCargo() == Cargo.GERENTE) {
            return propertyRepository.findAllByManager(user)
                    .stream()
                    .map(PropertyModel::toDto)
                    .collect(Collectors.toList());
        }

        throw new AccessDeniedException(
                "Acesso negado. Apenas usuários com o cargo 'PROPRIETARIO' ou 'GERENTE' podem acessar esta lista."
        );
    }

    /* ======================================================
       PRIVATE UPDATE HELPERS
    ====================================================== */

    private void updateNomeIfNecessary(PropertyModel property,
                                       PropertyPostRequestDto dto,
                                       Long propertyId) {

        if (dto.getNome() == null) return;

        String novoNome = dto.getNome().trim();

        if (!novoNome.equals(property.getNome())) {
            propertyRepository.findByNome(novoNome)
                    .filter(p -> !p.getId().equals(propertyId))
                    .ifPresent(p -> {
                        throw new EntityExistsException("O nome '" + novoNome + "' já está em uso.");
                    });

            property.setNome(novoNome);
        }
    }

    private void updateCnpjIfNecessary(PropertyModel property,
                                       PropertyPostRequestDto dto,
                                       Long propertyId) {

        if (dto.getCnpj() == null) return;

        String novoCnpj = sanitizeCnpj(dto.getCnpj());

        if (!novoCnpj.equals(property.getCnpj())) {

            propertyRepository.findByCnpj(novoCnpj)
                    .filter(p -> !p.getId().equals(propertyId))
                    .ifPresent(p -> {
                        throw new EntityExistsException("O CNPJ '" + novoCnpj + "' já está em uso.");
                    });

            property.setCnpj(novoCnpj);
        }
    }

    private void updateEnderecoIfNecessary(PropertyModel property,
                                           PropertyPostRequestDto dto) {

        if (dto.getEndereco() != null) {
            property.setEndereco(dto.getEndereco().trim());
        }
    }

    private void updateLocalizacaoIfNecessary(PropertyModel property,
                                              PropertyPostRequestDto dto) {

        if (dto.getLocalizacao() != null) {
            property.setLocalizacao(buildLocalizacao(dto.getLocalizacao()));
        }
    }

    private void updateIdFotoIfNecessary(PropertyModel property,
                                         PropertyPostRequestDto dto) {
        if (dto.getIdFoto() != null) {
            property.setIdFoto(dto.getIdFoto());
        }
    }

    /* ======================================================
       PRIVATE UTIL METHODS
    ====================================================== */

    private String sanitizeCnpj(String cnpj) {
        return cnpj.replaceAll("\\D", "");
    }

    private void validateUniqueNome(String nome) {
        propertyRepository.findByNome(nome)
                .ifPresent(p -> {
                    throw new EntityExistsException("Uma propriedade com este Nome já existe: " + nome);
                });
    }

    private void validateUniqueCnpj(String cnpj) {
        propertyRepository.findByCnpj(cnpj)
                .ifPresent(p -> {
                    throw new EntityExistsException("Uma propriedade com este CNPJ já existe: " + cnpj);
                });
    }

    private Localizacao buildLocalizacao(com.migueltcc.fertintelligence.dto.property.LocalizacaoDto dto) {
        return new Localizacao(
                dto.getLatitude(),
                dto.getLatitudeGraus(),
                dto.getLatitudeMinutos(),
                dto.getLatitudeSegundos(),
                dto.getLatitudeDirection(),
                dto.getLongitude(),
                dto.getLongitudeGraus(),
                dto.getLongitudeMinutos(),
                dto.getLongitudeSegundos(),
                dto.getLongitudeDirection(),
                dto.getAltitude()
        );
    }

    private UserModel findUserByUsernameOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new EntityNotFoundException("Usuário não encontrado: " + username));
    }

    private PropertyModel findPropertyByIdOrThrow(Long propertyId) {
        return propertyRepository.findById(propertyId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Propriedade não encontrada com o ID: " + propertyId));
    }

    private void checkUserIsProprietarioOrSupreme(UserModel user) {
        if (user.getCargo() == Cargo.PROPRIETARIO || user.getCargo() == Cargo.USUARIO_SUPREMO) {
            return;
        }

        throw new AccessDeniedException(
                "Acesso negado. Apenas usuários com o cargo 'PROPRIETARIO' ou 'USUARIO_SUPREMO' podem gerenciar propriedades."
        );
    }

    private void checkUserIsProprietario(UserModel user) {
        if (user.getCargo() != Cargo.PROPRIETARIO
                && user.getCargo() != Cargo.USUARIO_SUPREMO) {
            throw new AccessDeniedException(
                    "Acesso negado. Apenas usuários com o cargo 'PROPRIETARIO' ou 'USUARIO_SUPREMO' podem gerenciar propriedades."
            );
        }
    }

    private void checkPropertyPermission(PropertyModel property, UserModel user) {

        if (property.getOwner().getId().equals(user.getId())) return;

        if (property.getManager() != null &&
                property.getManager().getId().equals(user.getId())) return;

        boolean hasApproval =
                propertyAccessRequestRepository
                        .findByPropertyAndRequesterAndStatus(
                                property,
                                user,
                                AccessRequestStatus.APPROVED
                        )
                        .isPresent();

        if (!hasApproval) {
            throw new AccessDeniedException(
                    "Você não tem permissão para acessar ou modificar este recurso."
            );
        }
    }
}
